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

CREATE TABLE IF NOT EXISTS Tx_Event (
  id bigint NOT NULL AUTO_INCREMENT,
  create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modify_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  service_name varchar(36) NOT NULL,
  instance_id varchar(36) NOT NULL,
  global_tx_id varchar(36) NOT NULL,
  local_tx_id varchar(36) NOT NULL,
  parent_tx_id varchar(36) DEFAULT NULL,
  type varchar(50) NOT NULL,
  compensation_method varchar(256) NOT NULL,
  retry_method varchar(256) DEFAULT NULL,
  expiry_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  payloads blob,
  retries int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS Command (
  id bigint NOT NULL AUTO_INCREMENT,
  eventId bigint NOT NULL UNIQUE,
  serviceName varchar(36) NOT NULL,
  instanceId varchar(36) NOT NULL,
  globalTxId varchar(36) NOT NULL,
  localTxId varchar(36) NOT NULL,
  parentTxId varchar(36) DEFAULT NULL,
  compensationMethod varchar(256) NOT NULL,
  payloads blob,
  status varchar(12),
  lastModified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  version bigint NOT NULL,
  PRIMARY KEY (id),
  INDEX saga_commands_index (id, eventId, globalTxId, localTxId, status)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS TxTimeout (
  id bigint NOT NULL AUTO_INCREMENT,
  eventId bigint NOT NULL UNIQUE,
  serviceName varchar(36) NOT NULL,
  instanceId varchar(36) NOT NULL,
  globalTxId varchar(36) NOT NULL,
  localTxId varchar(36) NOT NULL,
  parentTxId varchar(36) DEFAULT NULL,
  type varchar(50) NOT NULL,
  expiryTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status varchar(12),
  version bigint NOT NULL,
  PRIMARY KEY (id),
  INDEX saga_timeouts_index (id, expiryTime, globalTxId, localTxId, status)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS tcc_global_tx_event (
  id bigint NOT NULL AUTO_INCREMENT,
  globalTxId varchar(36) NOT NULL,
  localTxId varchar(36) NOT NULL,
  parentTxId varchar(36) DEFAULT NULL,
  serviceName varchar(36) NOT NULL,
  instanceId varchar(36) NOT NULL,
  txType varchar(12),
  status varchar(12),
  creationTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX tcc_global_tx_event_index (globalTxId, localTxId, parentTxId, txType)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS tcc_participate_event (
  id bigint NOT NULL AUTO_INCREMENT,
  serviceName varchar(36) NOT NULL,
  instanceId varchar(36) NOT NULL,
  globalTxId varchar(36) NOT NULL,
  localTxId varchar(36) NOT NULL,
  parentTxId varchar(36) DEFAULT NULL,
  confirmMethod varchar(256) NOT NULL,
  cancelMethod varchar(256) NOT NULL,
  status varchar(50) NOT NULL,
  creationTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX tcc_participate_event_index (globalTxId, localTxId, parentTxId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS tcc_tx_event (
  id bigint NOT NULL AUTO_INCREMENT,
  globalTxId varchar(36) NOT NULL,
  localTxId varchar(36) NOT NULL,
  parentTxId varchar(36) DEFAULT NULL,
  serviceName varchar(36) NOT NULL,
  instanceId varchar(36) NOT NULL,
  methodInfo varchar(512) NOT NULL,
  txType varchar(12),
  status varchar(12),
  creationTime datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  lastModified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX tcc_tx_event_index (globalTxId, localTxId, parentTxId, txType)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
