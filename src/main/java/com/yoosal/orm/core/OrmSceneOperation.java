package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.query.Query;

import java.sql.SQLException;
import java.util.List;

public class OrmSceneOperation implements Operation {
    public static ThreadLocal<Operation> threadLocal = new ThreadLocal();

    @Override
    public void begin() throws SQLException {
        getOperation().begin();
    }

    private Operation getOperation() {
        if (threadLocal.get() == null) {
            threadLocal.set(new FrameworkOperation(OperationManager.getDataSourceManager()));
        }
        return threadLocal.get();
    }

    @Override
    public ModelObject save(ModelObject object) {
        return getOperation().save(object);
    }

    @Override
    public void update(ModelObject object) {
        getOperation().update(object);
    }

    @Override
    public void updates(Batch batch) {
        getOperation().updates(batch);
    }

    @Override
    public void remove(Query query) {
        getOperation().remove(query);
    }

    @Override
    public List<ModelObject> list(Query query) {
        return getOperation().list(query);
    }

    @Override
    public ModelObject query(Query query) {
        return getOperation().query(query);
    }

    @Override
    public long count(Query query) {
        return getOperation().count(query);
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
