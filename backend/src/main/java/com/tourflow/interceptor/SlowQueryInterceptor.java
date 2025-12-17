package com.tourflow.interceptor;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class SlowQueryInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(SlowQueryInterceptor.class);
    private static final long SLOW_QUERY_THRESHOLD_MS = 500;

    @Override
    public String onPrepareStatement(String sql) {
        return sql;
    }

    @Override
    public void afterTransactionBegin(org.hibernate.Transaction tx) {
        // Log du début de transaction
    }

    @Override
    public void afterTransactionCompletion(org.hibernate.Transaction tx) {
        // Log de la fin de transaction
    }

    @Override
    public void beforeTransactionCompletion(org.hibernate.Transaction tx) {
        // Log avant la fin de transaction
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public boolean onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return false;
    }

    @Override
    public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
        // Log lors de la suppression de collection
    }

    @Override
    public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
        // Log lors de la recréation de collection
    }

    @Override
    public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
        // Log lors de la mise à jour de collection
    }

    @Override
    public void preFlush(Iterator entities) throws CallbackException {
        // Log avant le flush
    }

    @Override
    public void postFlush(Iterator entities) throws CallbackException {
        // Log après le flush
    }
}
