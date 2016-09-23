package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Query;

import java.sql.SQLException;
import java.util.List;

public class SessionOperationManager implements Operation {
    public static ThreadLocal<LocalSessionModel> threadLocal = new ThreadLocal();
    private static DBMapping mapping = OperationManager.getMapping();

    @Override
    public void setIsolation(Isolation isolation) throws SQLException {
        LocalSessionModel sessionModel = getOperation();
        sessionModel.getSessionOperation().setIsolation(isolation);
    }

    @Override
    public void begin() throws SQLException {
        LocalSessionModel sessionModel = getOperation();
        sessionModel.getSessionOperation().begin();
        sessionModel.setIsBegin(true);
    }

    private LocalSessionModel getOperation() {
        try {
            if (threadLocal.get() == null) {
                SessionOperation sessionOperation = new OrmSessionOperation(OperationManager.getDataSourceManager());
                sessionOperation.setDbMapping(mapping);
                threadLocal.set(new LocalSessionModel(sessionOperation));
            }
            return threadLocal.get();
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
            LocalSessionModel sessionModel = getOperation();
            return sessionModel.getSessionOperation().save(object);
        } finally {
            this.close();
        }
    }

    @Override
    public void update(ModelObject object) {
        try {
            LocalSessionModel sessionModel = getOperation();
            sessionModel.getSessionOperation().update(object);
        } finally {
            this.close();
        }
    }

    @Override
    public void updates(Batch batch) {
        try {
            LocalSessionModel sessionModel = getOperation();
            sessionModel.getSessionOperation().updates(batch);
        } finally {
            this.close();
        }
    }

    @Override
    public void remove(Query query) {
        try {
            LocalSessionModel sessionModel = getOperation();
            sessionModel.getSessionOperation().remove(query);
        } finally {
            this.close();
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        try {
            LocalSessionModel sessionModel = getOperation();
            return sessionModel.getSessionOperation().list(query);
        } finally {
            this.close();
        }
    }

    @Override
    public ModelObject query(Query query) {
        try {
            LocalSessionModel sessionModel = getOperation();
            return sessionModel.getSessionOperation().query(query);
        } finally {
            this.close();
        }
    }

    @Override
    public long count(Query query) {
        try {
            LocalSessionModel sessionModel = getOperation();
            return sessionModel.getSessionOperation().count(query);
        } finally {
            this.close();
        }
    }

    @Override
    public void commit() throws SQLException {
        try {
            LocalSessionModel sessionModel = getOperation();
            sessionModel.setIsBegin(false);
            if (sessionModel.canCommit()) {
                sessionModel.getSessionOperation().commit();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            this.close();
        }
    }

    @Override
    public void rollback() {
        try {
            LocalSessionModel sessionModel = getOperation();
            if (sessionModel.canCommit()) {
                sessionModel.getSessionOperation().rollback();
            }
            sessionModel.setIsBegin(false);
        } finally {
            this.close();
        }
    }

    private void close() {
        LocalSessionModel sessionModel = getOperation();
        sessionModel.close(threadLocal);
    }
}
