package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Query;

import java.sql.SQLException;
import java.util.List;

public class SessionOperationManager implements Operation {
    public static ThreadLocal<SessionOperation> threadLocal = new ThreadLocal();
    public static ThreadLocal<Boolean> isBegin = new ThreadLocal();
    private static DBMapping mapping = OperationManager.getMapping();

    @Override
    public void begin() throws SQLException {
        getOperation().begin();
        isBegin.set(true);
    }

    private SessionOperation getOperation() {
        try {
            if (isBegin.get() != null && isBegin.get()) {
                if (threadLocal.get() == null) {
                    SessionOperation sessionOperation = new OrmSessionOperation(OperationManager.getDataSourceManager());
                    sessionOperation.setDbMapping(mapping);
                    threadLocal.set(sessionOperation);
                }
                return threadLocal.get();
            } else {
                SessionOperation sessionOperation = new OrmSessionOperation(OperationManager.getDataSourceManager());
                sessionOperation.setDbMapping(mapping);
                return sessionOperation;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ModelObject save(ModelObject object) {
        try {
            return getOperation().save(object);
        } finally {
            this.close();
        }
    }

    @Override
    public void update(ModelObject object) {
        try {
            getOperation().update(object);
        } finally {
            this.close();
        }
    }

    @Override
    public void updates(Batch batch) {
        try {
            getOperation().updates(batch);
        } finally {
            this.close();
        }
    }

    @Override
    public void remove(Query query) {
        try {
            getOperation().remove(query);
        } finally {
            this.close();
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        try {
            return getOperation().list(query);
        } finally {
            this.close();
        }
    }

    @Override
    public ModelObject query(Query query) {
        try {
            return getOperation().query(query);
        } finally {
            this.close();
        }
    }

    @Override
    public long count(Query query) {
        try {
            return getOperation().count(query);
        } finally {
            this.close();
        }
    }

    @Override
    public void commit() throws SQLException {
        getOperation().commit();
        isBegin.set(false);
    }

    @Override
    public void rollback() {
        getOperation().rollback();
        isBegin.set(false);
    }

    private void close() {
        if (isBegin.get() == null || !isBegin.get()) {
            getOperation().close();
        }
    }
}
