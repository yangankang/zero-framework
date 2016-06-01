package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.dialect.SQLDialect;
import com.yoosal.orm.dialect.SQLDialectFactory;
import com.yoosal.orm.exception.DatabaseOperationException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Query;

import javax.sql.DataSource;
import java.sql.*;
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
                String sql = sqlDialect.insert(tableModel, object);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
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
                    if (cm.getIsPrimaryKey() > 0 && cm.getIDStrategy() != null) {
                        object.put(cm.getJavaName(), cm.getIDStrategy().getOne(cm));
                    }
                }
                //通过映射表和对象生成一个insert的SQL
                String sql = sqlDialect.insert(tableModel, object);
                statement = connection.prepareStatement(sql);
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

    }

    @Override
    public void updates(List<ModelObject> objects) {

    }

    @Override
    public void remove(Query query) {

    }

    @Override
    public List<ModelObject> list(Query query) {
        return null;
    }

    @Override
    public ModelObject query(Query query) {
        return null;
    }

    @Override
    public long count(Query query) {
        return 0;
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
