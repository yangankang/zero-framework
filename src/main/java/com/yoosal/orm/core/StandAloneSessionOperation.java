package com.yoosal.orm.core;

import com.yoosal.common.Logger;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.dialect.CreatorJoinModel;
import com.yoosal.orm.dialect.SQLDialect;
import com.yoosal.orm.dialect.SQLDialectFactory;
import com.yoosal.orm.dialect.ValuesForPrepared;
import com.yoosal.orm.exception.DatabaseOperationException;
import com.yoosal.orm.exception.SessionException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.query.Wheres;
import com.yoosal.orm.transaction.StandAloneTransaction;
import com.yoosal.orm.transaction.Transaction;
import com.yoosal.orm.transaction.TransactionManger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class StandAloneSessionOperation implements SessionOperation {
    private static final Logger logger = Logger.getLogger(StandAloneSessionOperation.class);
    private DataSourceManager dataSourceManager;
    private Connection connection = null;
    private Connection slaveConnection = null;
    private SQLDialect dialect = null;
    private DBMapping dbMapping;

    public void setDbMapping(DBMapping dbMapping) {
        this.dbMapping = dbMapping;
    }

    private void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        if (slaveConnection != null) {
            try {
                slaveConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            if (connection != null && connection.isClosed()) {
                connection = null;
            }

            if (connection == null || !connection.getAutoCommit()) {
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection != null) {
            return connection;
        }
        DataSource dataSource = dataSourceManager.getMasterDataSource();
        if (dataSource == null) {
            throw new SessionException("there is no dataSource");
        }
        connection = dataSource.getConnection();
        return connection;
    }

    private Connection getQueryConnection(Query query) throws SQLException {
        if (query.isMaster()) {
            return getConnection();
        } else {
            if (slaveConnection != null) {
                return slaveConnection;
            }
            DataSource slaveDataSource = dataSourceManager.getSlaveDataSource();
            if (slaveDataSource == null) {
                throw new SessionException("there is no slave dataSource");
            }
            slaveConnection = slaveDataSource.getConnection();
            return slaveConnection;
        }
    }

    private Connection getSlaveConnection() throws SQLException {
        if (slaveConnection != null) {
            return slaveConnection;
        }
        DataSource dataSource = dataSourceManager.getSlaveDataSource();
        if (dataSource == null) {
            throw new SessionException("there is no dataSource");
        }
        slaveConnection = dataSource.getConnection();
        return slaveConnection;
    }

    private SQLDialect getDialect(Connection connection) throws SQLException {
        if (dialect == null) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            SQLDialect sqlDialect = SQLDialectFactory.getSQLDialect(databaseMetaData);
            dialect = sqlDialect;
        }
        return dialect;
    }

    @Override
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public Transaction beginTransaction() throws SQLException {
        return TransactionManger.getStandAloneTransaction(this, this.getConnection());
    }

    @Override
    public Transaction createTransaction() {
        Transaction transaction = null;
        try {
            transaction = TransactionManger.getStandAloneTransaction(this, this.getConnection());
        } catch (SQLException e) {
            transaction = new StandAloneTransaction();
            e.printStackTrace();
        }

        return transaction;
    }

    @Override
    public boolean isTransacting() {
        try {
            return !getConnection().getAutoCommit();
        } catch (SQLException e) {
        }
        return false;
    }

    @Override
    public ModelObject save(ModelObject object) {
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(object.getObjectClass());
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
                statement.execute();
                return object;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("save throw", e);
        } finally {
            close(statement);
        }
    }

    @Override
    public void update(ModelObject object) {
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(object.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareUpdate(tableModel, object);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("update throw", e);
        } finally {
            close(statement);
        }
    }

    @Override
    public void update(ModelObject editor, ModelObject criteria) {
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(editor.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareUpdate(tableModel, editor, criteria);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("update throw", e);
        } finally {
            close(statement);
        }
    }

    @Override
    public void updates(Batch batch) {
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
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
            close(statement);
        }
    }

    @Override
    public void remove(Query query) {
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareDelete(dbMapping, query);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseOperationException("remove throw", e);
        } finally {
            close(statement);
        }
    }

    @Override
    public List<ModelObject> list(Query query) {
        PreparedStatement statement = null;
        try {
            Connection connection = getQueryConnection(query);
            SQLDialect sqlDialect = getDialect(connection);
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareSelect(dbMapping, query);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);

            CreatorJoinModel model = valuesForPrepared.getModel();
            List<CreatorJoinModel> joinModels = model.getSelectColumns();
            ResultSet resultSet = statement.executeQuery();
            Map<CreatorJoinModel, List<ModelObject>> results = new LinkedHashMap<CreatorJoinModel, List<ModelObject>>();
            while (resultSet.next()) {
                for (CreatorJoinModel jm : joinModels) {
                    List<ModelObject> ol = results.get(jm);
                    if (ol == null) {
                        ol = new ArrayList<ModelObject>();
                    }

                    Map<String, String> javaColumnAdName = jm.getJavaColumnAsName();
                    ModelObject object = new ModelObject();
                    for (Map.Entry<String, String> entry : javaColumnAdName.entrySet()) {
                        object.put(entry.getKey(), resultSet.getObject(entry.getValue()));
                    }
                    ol.add(object);
                    results.put(jm, ol);
                }
            }

            return pressResult(results);
        } catch (SQLException e) {
            throw new DatabaseOperationException("list throw", e);
        } finally {
            close(statement);
        }
    }

    private List<ModelObject> pressResult(Map<CreatorJoinModel, List<ModelObject>> results) {
        Map<Class, List<ModelObject>> datas = new HashMap<Class, List<ModelObject>>();


        List<ModelObject> result = null;
        for (Map.Entry<CreatorJoinModel, List<ModelObject>> entry : results.entrySet()) {
            List<ModelObject> dos = distinct(entry.getValue(), entry.getKey().getTableModel());
            datas.put(
                    entry.getKey().isQuery() ? entry.getKey().getQuery().getObjectClass() : entry.getKey().getJoin().getObjectClass(),
                    dos
            );
            if (entry.getKey().isQuery()) {
                result = dos;
            }
        }

        if (result == null) {
            return null;
        }

        for (Map.Entry<CreatorJoinModel, List<ModelObject>> entry : results.entrySet()) {
            if (!entry.getKey().isQuery()) {
                Join join = entry.getKey().getJoin();
                Class a = join.getObjectClass();
                Class b = join.getSourceObjectClass();

                List<Wheres> wheres = join.getWheres();
                boolean isMulti = join.isMulti();
                List<ModelObject> aoes = datas.get(a);
                /**
                 * 假如b为空那么当前join的左表就是Query的类
                 */
                List<ModelObject> boes = b == null ? result : datas.get(b);
                /**
                 * 对左表类的数据遍历,然后在遍历右表类的数据,判断如果wheres相等就加入
                 */
                for (ModelObject bo : boes) {
                    for (ModelObject ao : aoes) {
                        boolean isConform = true;
                        for (Wheres wh : wheres) {
                            Object whereObject = wh.getValue();
                            if (whereObject.getClass().isEnum()) {
                                if (!this.isSameValue(bo.get(wh.getKey()), ao.get(whereObject))) {
                                    isConform = false;
                                }
                            } else {
                                if (!this.isSameValue(ao.get(wh.getKey()), whereObject)) {
                                    isConform = false;
                                }
                            }
                        }
                        /**
                         * 确认wheres对应的值是相等的那么就把当前的ao加到bo得子项中
                         */
                        if (isConform) {
                            if (isMulti) {
                                List<ModelObject> chOs = bo.getModelArray(join.getJoinName());
                                if (chOs == null) {
                                    chOs = new ArrayList<ModelObject>();
                                }
                                chOs.add(ao);
                                bo.put(join.getJoinName(), chOs);
                            } else {
                                bo.put(join.getJoinName(), ao);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<ModelObject> distinct(List<ModelObject> objects, TableModel tableModel) {
        List<ColumnModel> models = tableModel.getMappingPrimaryKeyColumnModels();
        List<ModelObject> arrayNewObjects = new ArrayList<ModelObject>();
        Map<String, ModelObject> hashMap = new LinkedHashMap<String, ModelObject>();
        for (ModelObject object : objects) {
            StringBuffer buffer = new StringBuffer();
            if (models != null) {
                for (ColumnModel cm : models) {
                    buffer.append(object.get(cm.getJavaName()));
                }
            } else {
                buffer.append(object.toJSONString());
            }
            hashMap.put(buffer.toString(), object);
        }

        for (Map.Entry<String, ModelObject> entry : hashMap.entrySet()) {
            arrayNewObjects.add(entry.getValue());
        }

        return arrayNewObjects;
    }

    private boolean isSameValue(Object a, Object b) {
        if (String.valueOf(a).equals(String.valueOf(b))) {
            return true;
        }
        return false;
    }

    @Override
    public ModelObject query(Query query) {
        List<ModelObject> objects = this.list(query);
        return (objects != null && objects.size() > 0) ? objects.get(0) : null;
    }

    @Override
    public long count(Query query) {
        PreparedStatement statement = null;
        long count = 0;
        try {
            connection = getConnection();
            SQLDialect sqlDialect = getDialect(connection);
            TableModel tableModel = dbMapping.getTableMapping(query.getObjectClass());
            ValuesForPrepared valuesForPrepared = sqlDialect.prepareSelectCount(tableModel, query);
            statement = connection.prepareStatement(valuesForPrepared.getSql());
            valuesForPrepared.setPrepared(statement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("count throw", e);
        } finally {
            close(statement);
        }
        return count;
    }
}
