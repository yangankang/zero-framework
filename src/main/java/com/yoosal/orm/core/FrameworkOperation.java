package com.yoosal.orm.core;

import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.OperationManager;
import com.yoosal.orm.exception.DatabaseOperationException;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.query.Wheres;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非线程安全的,每一个线程一个
 */
public class FrameworkOperation implements Operation {
    Map<String, Operation> singleOperations = new HashMap<String, Operation>();
    DBMapping mapping = OperationManager.getMapping();
    DataSourceManager dataSourceManager = null;
    private boolean isBegin = false;

    public FrameworkOperation(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public void begin() throws SQLException {
        for (Map.Entry<String, Operation> entry : singleOperations.entrySet()) {
            entry.getValue().begin();
        }
        isBegin = true;
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
        if (isBegin) {
            try {
                operation.begin();
            } catch (SQLException e) {
                throw new DatabaseOperationException("FrameworkOperation begin throw", e);
            }
        }
        return operation;
    }

    private String getDataSourceString(Query query) {
        String dataSourceName = query.getDataSourceName();
        if (StringUtils.isBlank(dataSourceName)) {
            dataSourceName = mapping.getTableMapping(query.getObjectClass()).getDataSourceName();
        }
        return dataSourceName;
    }

    private Operation getOperation(Query query) {
        String dataSourceName = getDataSourceString(query);

        if (singleOperations.get(dataSourceName) != null) {
            return singleOperations.get(dataSourceName);
        }
        Operation operation = new SingleDatabaseOperation(getDataSource(dataSourceName), mapping);
        singleOperations.put(dataSourceName, operation);
        return operation;
    }

    @Override
    public ModelObject save(ModelObject object) {
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

    private boolean isSameDataSource(Query query) {
        List<Join> joins = query.getJoins();
        String dataSourceNameString = getDataSourceString(query);
        for (Join join : joins) {
            String joinName = join.getDataSourceName();
            if (StringUtils.isBlank(joinName)) {
                joinName = mapping.getTableMapping(join.getObjectClass()).getDataSourceName();
            }
            if (dataSourceNameString != null) {
                if (!dataSourceNameString.equals(joinName)) {
                    return false;
                }
            } else {
                if (joinName != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private void joinToQuery(Join join, List<ModelObject> objects) {
        List<Wheres> wheres = join.getWheres();
        Class clazz = join.getObjectClass();
        String dataSourceName = join.getDataSourceName();
        Query query = new Query(clazz, dataSourceName);
        if (wheres.size() == 0) {
            return;
        } else if (wheres.size() == 1) {
            Wheres whs = wheres.get(0);
            String key = whs.getKey();
            String value = String.valueOf(whs.getValue());
            List<Object> values = new ArrayList<Object>();
            for (ModelObject object : objects) {
                values.add(object.get(key));
            }
            query.in(value, values);
            Operation operation = getOperation(query);
            List<ModelObject> joinObjects = operation.list(query);

            for (ModelObject object : objects) {
                String s1 = object.getString(key);
                List<ModelObject> in = new ArrayList<ModelObject>();
                for (ModelObject joinObject : joinObjects) {
                    String s2 = joinObject.getString(value);
                    if (s1.equals(s2)) {
                        in.add(joinObject);
                    }
                }
                object.put(join.getJoinName(), in);
            }
        } else {
            for (ModelObject object : objects) {
                for (Wheres whs : wheres) {
                    query.where(whs.getValue(), object.get(whs.getKey()));
                }
                Operation operation = getOperation(query);
                List<ModelObject> joinObjects = operation.list(query);
                object.put(join.getJoinName(), joinObjects);
            }
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        List<Join> joins = query.getJoins();
        Operation operation = getOperation(query);
        List<ModelObject> objects = operation.list(query);
       /* if (isSameDataSource(query)) {
        }*/
        for (Join join : joins) {
            joinToQuery(join, objects);
        }
        return objects;
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
