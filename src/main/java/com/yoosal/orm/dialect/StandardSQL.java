package com.yoosal.orm.dialect;

import com.yoosal.common.ClassUtils;
import com.yoosal.common.CollectionUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.exception.OrmMappingException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.TableModel;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class StandardSQL implements SQLDialect {
    protected static final Map<Integer, String> types = new HashMap<Integer, String>();
    protected static final Map<Class, String> typesMapping = new HashMap<Class, String>();
    protected static final int DEFAULT_LENGTH = 255;

    static {
        typesMapping.put(String.class, "VARCHAR");
        typesMapping.put(char.class, "CHAR");
        typesMapping.put(Integer.class, "INT");

        Field[] fields = Types.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                Integer code = (Integer) field.get(null);
                types.put(code, field.getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getType(int columnTypeInt) {
        return types.get(columnTypeInt);
    }

    @Override
    public String addColumn(TableModel tableModel, List<ColumnModel> existColumns) {
        String tableName = tableModel.getDbTableName();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("ALTER TABLE " + tableName);
        for (ColumnModel cm : existColumns) {
            long len = cm.getLength();
            String columnName = cm.getColumnName();
            Class clazz = cm.getJavaType();
            String columnType = typesMapping.get(clazz);
            int isPrimaryKey = cm.getIsPrimaryKey();
            Class strategy = cm.getGenerateStrategy();

            if (isPrimaryKey > 0) {
                columnType = typesMapping.get(Integer.class);
            }

            if (len <= 0 && clazz.isAssignableFrom(String.class) && isPrimaryKey <= 0) {
                len = DEFAULT_LENGTH;
            }

            String pkString = isPrimaryKey > 0 ? " PRIMARY KEY" : "";
            if (isPrimaryKey > 0) {
                if (strategy == null || strategy == Column.class) {
                    pkString += " AUTO_INCREMENT";
                }
            }

            sqlBuilder.append(" ADD ");
            String sql = columnName + " " + columnType + " " + (len > 0 ? "(" + len + ")" : "") + pkString;
            if (CollectionUtils.isLast(existColumns, cm)) {
                sqlBuilder.append(sql);
            } else {
                sqlBuilder.append(sql + ",");
            }
        }
        return sqlBuilder.toString();
    }

    public String createTable(TableModel tableModel) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + tableModel.getDbTableName() + "(");
        List<ColumnModel> columnModelList = tableModel.getMappingColumnModels();
        StringBuilder indexSQLBuilder = new StringBuilder();
        for (ColumnModel cm : columnModelList) {
            String columnName = cm.getColumnName();
            Class clazz = cm.getJavaType();
            String dbTypeName = typesMapping.get(clazz);
            long length = cm.getLength();
            Class strategy = cm.getGenerateStrategy();
            int isPrimaryKey = cm.getIsPrimaryKey();

            sqlBuilder.append(columnName + " ");
            if (isPrimaryKey > 0) {
                dbTypeName = typesMapping.get(Integer.class);
            }

            if (length <= 0 && clazz.isAssignableFrom(String.class) && isPrimaryKey <= 0) {
                length = DEFAULT_LENGTH;
            }

            sqlBuilder.append(dbTypeName);
            sqlBuilder.append(length > 0 ? "(" + length + ")" : "");
            if (isPrimaryKey > 0) {
                sqlBuilder.append(" PRIMARY KEY");
                if (strategy == null || strategy == Column.class) {
                    sqlBuilder.append(" AUTO_INCREMENT");
                }
            }
            if (CollectionUtils.isLast(columnModelList, cm)) {
                indexSQLBuilder.append(columnName);
            } else {
                sqlBuilder.append(",");
                indexSQLBuilder.append(columnName + ",");
            }
        }
        if (StringUtils.isNotBlank(indexSQLBuilder.toString())) {
            sqlBuilder.append(",INDEX(" + indexSQLBuilder.toString() + ")");
        }
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    @Override
    public String insert(TableModel tableMapping, ModelObject object) {
        return null;
    }
}
