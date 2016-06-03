package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.query.Query;

import java.sql.SQLException;
import java.util.List;

public class SessionOperationManager implements Operation {
    public static ThreadLocal<SessionOperation> threadLocal = new ThreadLocal();

    @Override
    public void begin() throws SQLException {
        getOperation().begin();
    }

    private SessionOperation getOperation() {
        if (threadLocal.get() == null) {
            threadLocal.set(new OrmSessionOperation(OperationManager.getDataSourceManager()));
        }
        return threadLocal.get();
    }

    @Override
    public ModelObject save(ModelObject object) {
        try {
            return getOperation().save(object);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public void update(ModelObject object) {
        try {
            getOperation().update(object);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public void updates(Batch batch) {
        try {
            getOperation().updates(batch);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public void remove(Query query) {
        try {
            getOperation().remove(query);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        try {
            return getOperation().list(query);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public ModelObject query(Query query) {
        try {
            return getOperation().query(query);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public long count(Query query) {
        try {
            return getOperation().count(query);
        } finally {
            getOperation().close();
        }
    }

    @Override
    public void commit() throws SQLException {
        getOperation().commit();
    }

    @Override
    public void rollback() {
        getOperation().rollback();
    }
}
