package com.yoosal.orm.mapping;

import com.yoosal.common.AnnotationUtils;
import com.yoosal.common.CollectionUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;
import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.core.GroupDataSource;
import com.yoosal.orm.dialect.MySQLDialect;
import com.yoosal.orm.dialect.SQLDialect;
import com.yoosal.orm.exception.OrmMappingException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDBMapping implements DBMapping {
    private static final Map<String, SQLDialect> registerSQLDialect = new ConcurrentHashMap<String, SQLDialect>();

    private DataSourceManager dataSourceManager;
    private Set<Class> classes;
    private Map<Class, TableModel> mappingModelMap = new HashMap<Class, TableModel>();

    static {
        registerSQLDialect.put(DataSourceManager.SupportList.MYSQL.toString(), new MySQLDialect());
    }

    @Override
    public void doMapping(DataSourceManager dataSourceManager, Set<Class> classes, boolean canAlter) throws SQLException {
        this.dataSourceManager = dataSourceManager;
        this.classes = classes;

        classToModel();
        compareToTables(canAlter);
    }

    @Override
    public void register(SQLDialect dialect) {
        registerSQLDialect.put(dialect.getDBType(), dialect);
    }

    @Override
    public SQLDialect getSQLDialect(DatabaseMetaData databaseMetaData) throws SQLException {
        String dataBaseName = databaseMetaData.getDatabaseProductName();
        for (Map.Entry<String, SQLDialect> entry : registerSQLDialect.entrySet()) {
            String key = entry.getKey();
            if (dataBaseName.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void compareToTables(boolean canAlter) throws SQLException {
        Set<GroupDataSource> groupDataSources = this.dataSourceManager.getAllDataSource();
        for (GroupDataSource groupDataSource : groupDataSources) {
            compareToTable(groupDataSource, canAlter);
        }
    }

    private void compareToTable(GroupDataSource groupDataSource, boolean canAlter) throws SQLException {
        List<String> enumNames = groupDataSource.getEnumNames();
        List<TableModel> tableModels = new ArrayList<TableModel>();
        if (!CollectionUtils.isEmpty(enumNames)) {
            for (Map.Entry<Class, TableModel> entry : mappingModelMap.entrySet()) {
                if (enumNames.contains(entry.getValue().getJavaTableName())) {
                    tableModels.add(entry.getValue());
                }
            }
        } else {
            for (Map.Entry<Class, TableModel> entry : mappingModelMap.entrySet()) {
                tableModels.add(entry.getValue());
            }
        }

        Set<GroupDataSource.SourceObject> sourceObjects = groupDataSource.getSourceObjects();
        for (GroupDataSource.SourceObject sourceObject : sourceObjects) {
            DataSource dataSource = sourceObject.getDataSource();
            compareDatabase(tableModels, dataSource, canAlter);
        }
    }

    private void compareDatabase(List<TableModel> tableModels, DataSource dataSource, boolean canAlter) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            String[] type = {"Table"};
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet tableResultSet = databaseMetaData.getTables(connection.getCatalog(), null, null, type);
            List<String> tableNames = new ArrayList<String>();
            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                tableNames.add(tableName.toLowerCase());
            }

            for (TableModel tableModel : tableModels) {
                if (tableNames.contains(tableModel.getDbTableName().toLowerCase())) {
                    ResultSet columnResultSet = databaseMetaData.getColumns(connection.getCatalog(), null, tableModel.getDbTableName(), null);
                    List<ColumnModel> columnModels = tableModel.getMappingColumnModels();
                    List<ColumnModel> existColumns = new ArrayList<ColumnModel>();
                    for (ColumnModel cm : columnModels) {
                        existColumns.add(cm);
                    }
                    while (columnResultSet.next()) {
                        String columnName = columnResultSet.getString("COLUMN_NAME");
                        String typeName = columnResultSet.getString("TYPE_NAME");
                        int dataType = columnResultSet.getInt("DATA_TYPE");

                        String columnType = getSQLDialect(databaseMetaData).getType(dataType);
                        ColumnModel columnModel = null;
                        for (ColumnModel cm : columnModels) {
                            if (cm.getColumnName().equalsIgnoreCase(columnName)) {
                                columnModel = cm;
                                break;
                            }
                        }
                        if (columnModel != null) {
                            existColumns.remove(columnModel);
                            columnModel.setColumnType(columnType);
                            columnModel.setColumnTypeCode(dataType);
                        }
                    }
                    if (existColumns.size() > 0) {
                        if (canAlter) {
                            //如果可以修改数据库表结构，那么就将新增的字段增加到表中
                            this.alertAddColumn(dataSource, tableModel, existColumns);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (ColumnModel cm : existColumns) {
                                sb.append(cm.getJavaName() + ":" + cm.getColumnName() + " ");
                            }
                            throw new OrmMappingException("can't alter table and columns mapping match the inconsistent " + sb.toString());
                        }
                    }
                    statement.close();
                } else {
                    if (canAlter) {
                        //如果可以修改数据库表结构，那么新增表
                        this.createTable(dataSource, tableModel);
                    } else {
                        throw new OrmMappingException("can't alter table mapping match the inconsistent " + tableModel.getJavaTableName() + ":" + tableModel.getDbTableName());
                    }
                }
            }
            connection.close();
        } finally {
            ccs(connection, statement);
        }
    }

    private void ccs(Connection connection, Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    private void alertAddColumn(DataSource dataSource, TableModel tableModel, List<ColumnModel> existColumns) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String sql = getSQLDialect(databaseMetaData).addColumn(tableModel, existColumns);
            statement = connection.createStatement();
            boolean success = statement.execute(sql);
        } finally {
            ccs(connection, statement);
        }
    }

    private void createTable(DataSource dataSource, TableModel tableModel) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String sql = getSQLDialect(databaseMetaData).createTable(tableModel);
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            boolean success = statement.execute(sql);
        } finally {
            ccs(connection, statement);
        }
    }

    private void classToModel() {
        if (classes != null) {
            for (Class clazz : classes) {
                Table table = AnnotationUtils.findAnnotation(clazz, Table.class);
                if (table == null) continue;
                TableModel model = new TableModel();
                String tableName = table.value();
                model.setJavaTableName(tableName);
                String dataSourceName = table.dataSourceName();
                if (StringUtils.isNotBlank(dataSourceName)) {
                    model.setDataSourceName(dataSourceName);
                }
                model.convert();

                Object[] objects = clazz.getEnumConstants();
                int i = 0;
                for (Object obj : objects) {
                    ColumnModel columnModel = new ColumnModel();
                    columnModel.setCode(i);
                    columnModel.setJavaName(obj.toString());
                    Column column = null;
                    try {
                        column = clazz.getField(obj.toString()).getAnnotation(Column.class);
                    } catch (NoSuchFieldException e) {
                        throw new OrmMappingException("not find Column in field " + tableName + "." + obj.toString(), e);
                    }
                    String name = column == null ? null : column.name();
                    if (StringUtils.isBlank(name)) name = null;
                    Class type = column == null ? String.class : column.type();
                    long length = column == null ? 255 : column.length();
                    if (length == 255 && type == Integer.class) length = 11;
                    if (length == 255 && type == Long.class) length = 13;
                    if (length == 255 && type == Double.class) length = 16;
                    if (length == 255 && type == Text.class) length = 0;
                    int isPrimaryKey = (column == null ? 0 : column.key());
                    Class generateStrategy = (column == null ? null : column.strategy());
                    if (generateStrategy != null && generateStrategy.isAssignableFrom(Column.class))
                        generateStrategy = null;
                    boolean isLock = (column == null ? false : column.lock());
                    columnModel.setIsPrimaryKey(isPrimaryKey);
                    columnModel.setJavaAliasName(name);
                    columnModel.setJavaType(type);
                    columnModel.setLength(length);
                    columnModel.setGenerateStrategy(generateStrategy);
                    columnModel.setLock(isLock);
                    columnModel.convert();
                    model.addMappingColumnModel(columnModel);
                    i++;
                }
                mappingModelMap.put(clazz, model);
            }
        }
    }
}
