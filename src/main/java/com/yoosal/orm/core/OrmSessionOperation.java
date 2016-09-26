package com.yoosal.orm.core;

import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * 非线程安全的,每一个线程一个
 */
public class OrmSessionOperation implements SessionOperation {
    private static final String sessionOperationClass = OperationManager.getSessionOperation();
    private DBMapping mapping = null;
    private Class soClass;
    private SessionOperation sessionOperation;
    private DataSourceManager dataSourceManager;

    public OrmSessionOperation(DataSourceManager dataSourceManager) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.dataSourceManager = dataSourceManager;
        if (StringUtils.isNotBlank(sessionOperationClass)) {
            soClass = Class.forName(sessionOperationClass);
        }
        setSessionOperation();
    }

    private SessionOperation setSessionOperation() throws IllegalAccessException, InstantiationException {
        sessionOperation = (SessionOperation) soClass.newInstance();
        sessionOperation.setDataSourceManager(this.dataSourceManager);
        sessionOperation.setDbMapping(mapping);
        return sessionOperation;
    }

    @Override
    public void close() {
        sessionOperation.close();
    }

    @Override
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public void setDbMapping(DBMapping dbMapping) {
        mapping = dbMapping;
        sessionOperation.setDbMapping(mapping);
    }

    @Override
    public Transaction beginTransaction() throws SQLException {
        return sessionOperation.beginTransaction();
    }

    @Override
    public Transaction createTransaction() {
        return sessionOperation.createTransaction();
    }

    @Override
    public boolean isTransacting() {
        return sessionOperation.isTransacting();
    }

    @Override
    public ModelObject save(ModelObject object) {
        return sessionOperation.save(object);
    }

    @Override
    public void update(ModelObject object) {
        sessionOperation.update(object);
    }

    @Override
    public void updates(Batch batch) {
        sessionOperation.updates(batch);
    }

    @Override
    public void remove(Query query) {
        sessionOperation.remove(query);
    }

    @Override
    public List<ModelObject> list(Query query) {
        return sessionOperation.list(query);
    }

    @Override
    public ModelObject query(Query query) {
        return sessionOperation.query(query);
    }

    @Override
    public long count(Query query) {
        return sessionOperation.count(query);
    }
}
