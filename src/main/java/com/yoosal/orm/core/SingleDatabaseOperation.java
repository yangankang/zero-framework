package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.dialect.SQLDialect;
import com.yoosal.orm.dialect.SQLDialectFactory;
import com.yoosal.orm.dialect.ValuesForPrepared;
import com.yoosal.orm.exception.DatabaseOperationException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.query.Wheres;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SingleDatabaseOperation implements Operation {
    private DataSource dataSource;
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal();
    private SQLDialect dialect = null;
    private DBMapping dbMapping;

    public SingleDatabaseOperation(DataSource dataSource, DBMapping dbMapping) {
        this.dataSource = dataSource;
        this.dbMapping = dbMapping;
        if (dataSource == null || dbMapping == null) {
            throw new DatabaseOperationException("the dataSource and dbMapping must be");
        }
    }

    private Connection getConnection() throws SQLException {
        Connection cnn = connectionThreadLocal.get();
        if (cnn == null) {
            cnn = dataSource.getConnection();
        }
        return cnn;
    }

    private SQLDialect getDialect(Connection connection) throws SQLException {
        if (dialect == null) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            SQLDialect sqlDialect = SQLDialectFactory.getSQLDialect(databaseMetaData);
            dialect = sqlDialect;
        }
        return dialect;
    }

    private void close(Connection connection, Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void begin() {
        try {
            Connection connection = getConnection();
            connectionThreadLocal.set(connection);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DatabaseOperationException("get connection throw", e);
        }
    }

    @Override
    public Object save(ModelObject object) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(object.getClass());
            connection = dataSource.getConnection();
            List<ColumnModel> primaryKeyColumns = tableModel.getMappingPrimaryKeyColumnModels();
            Boolean hasAutoIncrementPrimaryKey = tableModel.hasAutoIncrementPrimaryKey();
            if (hasAutoIncrementPrimaryKey) {
                ValuesForPrepared valuesForPrepared = sqlDialect.prepareInsert(tableModel, object);
                statement = connection.prepareStatement(valuesForPrepared.getSql(), Statement.RETURN_GENERATED_KEYS);
                valuesForPrepared.setPrepared(statement);
                statement.executeUpdate();
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    long primaryKeyValue = rs.getLong(1);
                    for (ColumnModel cm : primaryKeyColumns) {
                        if (cm.isAutoIncrement()) {
                            object.put(cm.getJavaName(), primaryKeyValue);
                        }
                    }
                }
                return object;
            } else {
                /**
                 * 如果没有自增长的主键则直接执行添加，非自增长的ID，ID的值是由generateStrategy决定的
                 * 添加前会执行获得一个ID
                 */
                for (ColumnModel cm : primaryKeyColumns) {
                    if (cm.isPrimaryKey() && cm.getIDStrategy() != null) {
                        object.put(cm.getJavaName(), cm.getIDStrategy().getOne(cm));
                    }
                }
                //通过映射表和对象生成一个insert的SQL
                ValuesForPrepared valuesForPrepared = sqlDialect.prepareInsert(tableModel, object);
                statement = connection.prepareStatement(valuesForPrepared.getSql());
                valuesForPrepared.setPrepared(statement);
                boolean isSuccess = statement.execute();
                if (!isSuccess) {
                    throw new DatabaseOperationException("statement execute return false");
                }
                return object;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("save throw", e);
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public void update(ModelObject object) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(object.getObjectClass());
            connection = dataSource.getConnection();
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareUpdate(tableModel, object);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("update throw", e);
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public void updates(Batch batch) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            SQLDialect sqlDialect = getDialect(connection);
            connection = dataSource.getConnection();
            TableModel tableModel = dbMapping.getTableMapping(batch.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareUpdateBatch(tableModel, batch);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            for (ModelObject object : batch.getObjects()) {
                valuesForPrepared.setPrepared(statement, object);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseOperationException("updates throw", e);
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public void remove(Query query) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            List<Wheres> wheres = query.getWheres();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(query.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareDelete(tableModel, wheres);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseOperationException("remove throw", e);
        } finally {
            close(connection, statement);
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        Connection connection = null;
        PreparedStatement statement = null;
        List<ModelObject> objects = null;
        try {
            List<Wheres> wheres = query.getWheres();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(query.getObjectClass());
            List<ColumnModel> columnModels = tableModel.getMappingColumnModels();
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareSelect(tableModel, wheres);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (objects == null) {
                    objects = new ArrayList<ModelObject>();
                }
                ModelObject object = new ModelObject();
                for (ColumnModel cm : columnModels) {
                    object.put(cm.getJavaName(), resultSet.getObject(cm.getColumnName()));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("remove throw", e);
        } finally {
            close(connection, statement);
        }
        return objects;
    }

    @Override
    public ModelObject query(Query query) {
        List<ModelObject> objects = this.list(query);
        if (objects != null && objects.size() > 0) {
            return objects.get(0);
        }
        return null;
    }

    @Override
    public long count(Query query) {
        Connection connection = null;
        PreparedStatement statement = null;
        long count = 0;
        try {
            List<Wheres> wheres = query.getWheres();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(query.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareSelectCount(tableModel, wheres);
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("remove throw", e);
        } finally {
            close(connection, statement);
        }
        return count;
    }

    @Override
    public void commit() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new DatabaseOperationException("commit throw", e);
            }
        }
    }

    @Override
    public void rollback() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new DatabaseOperationException("rollback throw", e);
            }
        }
    }
}
