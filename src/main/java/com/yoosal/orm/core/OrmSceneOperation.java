package com.yoosal.orm.core;

import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非线程安全的,每一个线程一个
 */
public class OrmSceneOperation implements Operation {
    Map<String, Operation> singleOperations = new HashMap<String, Operation>();
    DBMapping mapping = OperationManager.getMapping();
    DataSourceManager dataSourceManager = null;

    public OrmSceneOperation(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public void begin() throws SQLException {
        for (Map.Entry<String, Operation> entry : singleOperations.entrySet()) {
            entry.getValue().begin();
        }
    }

    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    private DataSource getDataSource(String dataSourceName) {
        return dataSourceManager.getDataSource(dataSourceName);
    }

    private Operation getOperation(Class clazz) {
        String dataSourceName = mapping.getTableMapping(clazz).getDataSourceName();
        if (singleOperations.get(dataSourceName) != null) {
            return singleOperations.get(dataSourceName);
        }
        Operation operation = new SingleDatabaseOperation(getDataSource(dataSourceName), mapping);
        singleOperations.put(dataSourceName, operation);
        return operation;
    }

    private Operation getOperation(Query query) {
        String dataSourceName = query.getDataSourceName();
        if (StringUtils.isBlank(dataSourceName)) {
            dataSourceName = mapping.getTableMapping(query.getObjectClass()).getDataSourceName();
        }

        if (singleOperations.get(dataSourceName) != null) {
            return singleOperations.get(dataSourceName);
        }
        Operation operation = new SingleDatabaseOperation(getDataSource(dataSourceName), mapping);
        singleOperations.put(dataSourceName, operation);
        return operation;
    }

    @Override
    public Object save(ModelObject object) {
        Operation operation = getOperation(object.getObjectClass());
        return operation.save(object);
    }

    @Override
    public void update(ModelObject object) {
        Operation operation = getOperation(object.getObjectClass());
        operation.update(object);
    }

    @Override
    public void updates(Batch batch) {
        Operation operation = getOperation(batch.getObjectClass());
        operation.updates(batch);
    }

    @Override
    public void remove(Query query) {
        Operation operation = getOperation(query);
        operation.remove(query);
    }

    @Override
    public List<ModelObject> list(Query query) {
        List<Join> joins = query.getJoins();

        return null;
    }

    @Override
    public ModelObject query(Query query) {
        List<ModelObject> objects = list(query);
        if (objects != null && objects.size() > 0) {
            return objects.get(0);
        }
        return null;
    }

    @Override
    public long count(Query query) {
        Operation operation = getOperation(query);
        return operation.count(query);
    }

    @Override
    public void commit() throws SQLException {
        for (Map.Entry<String, Operation> entry : singleOperations.entrySet()) {
            entry.getValue().commit();
        }
    }

    @Override
    public void rollback() {
        for (Map.Entry<String, Operation> entry : singleOperations.entrySet()) {
            entry.getValue().rollback();
        }
    }
}
