package org.apache.servicecomb.saga.alpha.server.saga.dao;


import org.apache.servicecomb.saga.alpha.core.TxEvent;
import org.apache.servicecomb.saga.alpha.core.TxEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

public class TxEventDaoImpl implements TxEventRepository {

    private static final String SQL_SAVE = "insert into tx_event";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(TxEvent event) {

    }

    @Override
    public Optional<List<TxEvent>> findFirstAbortedGlobalTransaction() {
        return Optional.empty();
    }

    @Override
    public List<TxEvent> findTimeoutEvents() {
        return null;
    }

    @Override
    public Optional<TxEvent> findTxStartedEvent(String globalTxId, String localTxId) {
        return Optional.empty();
    }

    @Override
    public List<TxEvent> findTransactions(String globalTxId, String type) {
        return null;
    }

    @Override
    public List<TxEvent> findFirstUncompensatedEventByIdGreaterThan(long id, String type) {
        return null;
    }

    @Override
    public Optional<TxEvent> findFirstCompensatedEventByIdGreaterThan(long id) {
        return Optional.empty();
    }

    @Override
    public void deleteDuplicateEvents(String type) {

    }
}
