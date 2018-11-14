/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.saga.alpha.core;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.servicecomb.saga.alpha.core.TaskStatus.NEW;
import static org.apache.servicecomb.saga.common.EventType.SagaEndedEvent;
import static org.apache.servicecomb.saga.common.EventType.TxAbortedEvent;
import static org.apache.servicecomb.saga.common.EventType.TxEndedEvent;
import static org.apache.servicecomb.saga.common.EventType.TxStartedEvent;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import kamon.annotation.EnableKamon;
import kamon.annotation.Trace;

import org.apache.servicecomb.saga.common.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableKamon
public class EventScanner implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final byte[] EMPTY_PAYLOAD = new byte[0];

  private final ScheduledExecutorService scheduler;

  private final TxEventRepository eventRepository;

  private final CommandRepository commandRepository;

  private final TxTimeoutRepository timeoutRepository;

  private final OmegaCallback omegaCallback;

  private final int eventPollingInterval;

  private long nextEndedEventId;

  private long nextCompensatedEventId;

  public EventScanner(ScheduledExecutorService scheduler,
      TxEventRepository eventRepository,
      CommandRepository commandRepository,
      TxTimeoutRepository timeoutRepository,
      OmegaCallback omegaCallback,
      int eventPollingInterval) {
    this.scheduler = scheduler;
    this.eventRepository = eventRepository;
    this.commandRepository = commandRepository;
    this.timeoutRepository = timeoutRepository;
    this.omegaCallback = omegaCallback;
    this.eventPollingInterval = eventPollingInterval;
  }

  @Override
  public void run() {
    pollEvents();
  }

  private void pollEvents() {
    scheduler.scheduleWithFixedDelay(
        () -> {
          updateTimeoutStatus();
          findTimeoutEvents();
          abortTimeoutEvents();
          saveUncompensatedEventsToCommands();
          compensate();
          updateCompensatedCommands();
          deleteDuplicateSagaEndedEvents();
          updateTransactionStatus();
        },
        0,
        eventPollingInterval,
        MILLISECONDS);
  }

  @Trace("findTimeoutEvents")
  private void findTimeoutEvents() {
    eventRepository.findTimeoutEvents()
        .forEach(event -> {
          LOG.info("Found timeout event {}", event);
          timeoutRepository.save(txTimeoutOf(event));
        });
  }

  private void updateTimeoutStatus() {
    timeoutRepository.markTimeoutAsDone();
  }

  @Trace("saveUncompensatedEventsToCommands")
  private void saveUncompensatedEventsToCommands() {
    eventRepository.findFirstUncompensatedEventByIdGreaterThan(nextEndedEventId, TxEndedEvent.name())
        .forEach(event -> {
          LOG.info("Found uncompensated event {}", event);
          nextEndedEventId = event.getId();
          commandRepository.saveCompensationCommands(event.getGlobalTxId());
        });
  }

  @Trace("updateCompensationStatus")
  private void updateCompensatedCommands() {
    eventRepository.findFirstCompensatedEventByIdGreaterThan(nextCompensatedEventId)
        .ifPresent(event -> {
          LOG.info("Found compensated event {}", event);
          nextCompensatedEventId = event.getId();
          updateCompensationStatus(event);
        });
  }

  @Trace("deleteDuplicateSagaEndedEvents")
  private void deleteDuplicateSagaEndedEvents() {
    try {
      eventRepository.deleteDuplicateEvents(SagaEndedEvent.name());
    } catch (Exception e) {
      LOG.warn("Failed to delete duplicate event", e);
    }
  }

  private void updateCompensationStatus(TxEvent event) {
    commandRepository.markCommandAsDone(event.getGlobalTxId(), event.getLocalTxId());
    LOG.info("Transaction with globalTxId {} and localTxId {} was compensated",
        event.getGlobalTxId(),
        event.getLocalTxId());

    markSagaEnded(event);
  }

  @Trace("abortTimeoutEvents")
  private void abortTimeoutEvents() {
    timeoutRepository.findFirstTimeout().forEach(timeout -> {
      LOG.info("Found timeout event {} to abort", timeout);

      eventRepository.save(toTxAbortedEvent(timeout));

      if (timeout.type().equals(TxStartedEvent.name())) {
        eventRepository.findTxStartedEvent(timeout.globalTxId(), timeout.localTxId())
            .ifPresent(omegaCallback::compensate);
      }
    });
  }

  @Trace("updateTransactionStatus")
  private void updateTransactionStatus() {
    eventRepository.findFirstAbortedGlobalTransaction().ifPresent(this::markGlobalTxEndWithEvents);
  }

  private void markSagaEnded(TxEvent event) {
    if (commandRepository.findUncompletedCommands(event.getGlobalTxId()).isEmpty()) {
      markGlobalTxEndWithEvent(event);
    }
  }

  private void markGlobalTxEndWithEvent(TxEvent event) {
    eventRepository.save(toSagaEndedEvent(event));
    LOG.info("Marked end of transaction with globalTxId {}", event.getGlobalTxId());
  }

  private void markGlobalTxEndWithEvents(List<TxEvent> events) {
    events.forEach(this::markGlobalTxEndWithEvent);
  }

  private TxEvent toTxAbortedEvent(TxTimeout timeout) {

    return new TxEvent()
            .serviceName(timeout.serviceName())
            .instanceId(timeout.instanceId())
            .globalTxId(timeout.globalTxId())
            .localTxId(timeout.localTxId())
            .parentTxId(timeout.parentTxId())
            .type(TxAbortedEvent.name())
            .compensationMethod("")
            .payloads("Transaction timeout".getBytes());
  }

  private TxEvent toSagaEndedEvent(TxEvent event) {
    return new TxEvent()
            .globalTxId(event.getGlobalTxId())
            .localTxId(event.getGlobalTxId())
            .serviceName(event.getServiceName())
            .instanceId(event.getInstanceId())
            .parentTxId(null)
            .type(SagaEndedEvent.name())
            .payloads(EMPTY_PAYLOAD)
            .compensationMethod("")
            ;
  }

  @Trace("compensate")
  private void compensate() {
    commandRepository.findFirstCommandToCompensate()
        .forEach(command -> {
          LOG.info("Compensating transaction with globalTxId {} and localTxId {}",
              command.globalTxId(),
              command.localTxId());

          omegaCallback.compensate(txStartedEventOf(command));
        });
  }

  private TxEvent txStartedEventOf(Command command) {

    return new TxEvent()
            .serviceName(command.serviceName())
            .instanceId(command.instanceId())
            .globalTxId(command.globalTxId())
            .localTxId(command.localTxId())
            .parentTxId(command.parentTxId())
            .type(TxStartedEvent.name())
            .compensationMethod(command.compensationMethod())
            .payloads(command.payloads());
  }

  private TxTimeout txTimeoutOf(TxEvent event) {
    return new TxTimeout(
        event.getId(),
        event.getServiceName(),
        event.getInstanceId(),
        event.getGlobalTxId(),
        event.getLocalTxId(),
        event.getParentTxId(),
            event.getType(),
        event.getExpiryTime(),
        NEW.name());
  }
}
