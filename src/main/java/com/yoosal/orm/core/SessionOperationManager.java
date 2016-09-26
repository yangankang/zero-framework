package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public class SessionOperationManager implements Operation {
    public static ThreadLocal<LocalSessionModel> threadLocal = new ThreadLocal();
    private static DBMapping mapping = OperationManager.getMapping();

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
    public Transaction beginTransaction() throws SQLException {
        Transaction transaction = getOperation().getSessionOperation().beginTransaction();
        setTransactionCallback(transaction);
        return transaction;
    }

    @Override
    public Transaction createTransaction() {
        Transaction transaction = getOperation().getSessionOperation().createTransaction();
        setTransactionCallback(transaction);
        return transaction;
    }

    private void setTransactionCallback(Transaction transaction) {
        transaction.setCallback(new Transaction.CRCallback() {
            @Override
            public void call() {
                close();
            }
        });
    }

    @Override
    public boolean isTransacting() {
        return getOperation().getSessionOperation().isTransacting();
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

    private void close() {
        LocalSessionModel sessionModel = getOperation();
        sessionModel.close(threadLocal);
    }
}
