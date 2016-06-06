package com.yoosal.orm.dialect;

import com.yoosal.common.CollectionUtils;
import com.yoosal.common.Logger;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.core.Batch;
import com.yoosal.orm.exception.SQLDialectException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Wheres;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

public abstract class StandardSQL implements SQLDialect {
    private static final Logger logger = Logger.getLogger(StandardSQL.class);
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

    protected boolean isShowSQL = false;

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
            boolean isPrimaryKey = cm.isPrimaryKey();

            if (isPrimaryKey) {
                columnType = typesMapping.get(Integer.class);
            }

            if (len <= 0 && clazz.isAssignableFrom(String.class) && !isPrimaryKey) {
                len = DEFAULT_LENGTH;
            }

            String pkString = isPrimaryKey ? " PRIMARY KEY" : "";
            if (isPrimaryKey) {
                if (cm.isAutoIncrement()) {
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
        showSQL(sqlBuilder.toString());
        return sqlBuilder.toString();
    }

    public String createTable(TableModel tableModel) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE TABLE IF NOT EXISTS " + tableModel.getDbTableName() + "(");
        List<ColumnModel> columnModelList = tableModel.getMappingColumnModels();
        StringBuilder indexSQLBuilder = new StringBuilder();
        StringBuilder primaryKeySQLBuilder = new StringBuilder(",PRIMARY KEY (");
        for (ColumnModel cm : columnModelList) {
            String columnName = cm.getColumnName();
            Class clazz = cm.getJavaType();
            String dbTypeName = typesMapping.get(clazz);
            long length = cm.getLength();
            boolean isPrimaryKey = cm.isPrimaryKey();

            sqlBuilder.append(columnName + " ");
            if (isPrimaryKey) {
                dbTypeName = typesMapping.get(Integer.class);
            }

            if (length <= 0 && clazz.isAssignableFrom(String.class) && !isPrimaryKey) {
                length = DEFAULT_LENGTH;
            }

            sqlBuilder.append(dbTypeName);
            sqlBuilder.append(length > 0 ? "(" + length + ")" : "");
            if (isPrimaryKey) {
                primaryKeySQLBuilder.append(columnName + ",");
                if (cm.isAutoIncrement()) {
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
        if (tableModel.haPrimaryKey()) {
            sqlBuilder.append(primaryKeySQLBuilder.substring(0, primaryKeySQLBuilder.length() - 1) + ")");
        }
        if (StringUtils.isNotBlank(indexSQLBuilder.toString())) {
            sqlBuilder.append(",INDEX(" + indexSQLBuilder.toString() + ")");
        }
        sqlBuilder.append(")");
        showSQL(sqlBuilder.toString());
        return sqlBuilder.toString();
    }

    private boolean contains(Object[] wcs, ColumnModel cm) {
        for (Object object : wcs) {
            if (String.valueOf(object).equals(cm.getJavaName())) {
                return true;
            }
        }
        return false;
    }

    private List<ColumnModel> getValidateColumn(TableModel tableMapping, ModelObject object, List<ColumnModel> whereColumns) {
        List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
        Object[] ucs = object.getUpdateColumn();    //主键作为修改内容的数组
        Object[] wcs = object.getWhereColumn();     //作为修改条件的数组
        for (ColumnModel cm : tableMapping.getMappingColumnModels()) {
            if (object.containsKey(cm.getJavaName())) {
                if (whereColumns != null) {
                    if (contains(wcs, cm)) {
                        whereColumns.add(cm);
                    } else {
                        if (!cm.isPrimaryKey()) {
                            columnModels.add(cm);
                        } else {
                            if (contains(ucs, cm)) {
                                columnModels.add(cm);
                            } else {
                                whereColumns.add(cm);
                            }
                        }
                    }
                } else {
                    columnModels.add(cm);
                }
            }
        }
        return columnModels;
    }


    @Override
    public ValuesForPrepared prepareInsert(TableModel tableMapping, ModelObject object) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        List<ColumnModel> columnModels = getValidateColumn(tableMapping, object, null);
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        Iterator<ColumnModel> it = columnModels.iterator();
        while (it.hasNext()) {
            ColumnModel cm = it.next();
            key.append(cm.getColumnName());
            value.append(":" + cm.getJavaName());
            if (it.hasNext()) {
                key.append(",");
                value.append(",");
            }
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
        }

        valuesForPrepared.setSql("INSERT INTO " + tableMapping.getDbTableName() + " (" + key + ") VALUES (" + value + ")");
        showSQL(valuesForPrepared.getSql());
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareUpdate(TableModel tableMapping, ModelObject object) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        List<ColumnModel> whereColumnModels = new ArrayList<ColumnModel>();
        if (whereColumnModels.size() <= 0) {
            throw new SQLDialectException("update sql no where statement");
        }
        List<ColumnModel> columnModels = getValidateColumn(tableMapping, object, whereColumnModels);

        Iterator<ColumnModel> cmIt = columnModels.iterator();
        StringBuilder set = new StringBuilder();
        Iterator<ColumnModel> wcIt = whereColumnModels.iterator();
        StringBuilder where = new StringBuilder();
        while (cmIt.hasNext()) {
            ColumnModel cm = cmIt.next();
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
            set.append(cm.getColumnName() + "=:" + cm.getJavaName());
            if (cmIt.hasNext()) {
                set.append(" AND ");
            }
        }

        while (wcIt.hasNext()) {
            ColumnModel cm = wcIt.next();
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
            where.append(cm.getColumnName() + "=:" + cm.getJavaName());
            if (wcIt.hasNext()) {
                where.append(" AND ");
            }
        }

        valuesForPrepared.setSql("UPDATE " + tableMapping.getDbTableName() +
                " SET " + set +
                " WHERE " + where);

        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareUpdateBatch(TableModel tableMapping, Batch batch) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        List<ColumnModel> whereColumnModels = new ArrayList<ColumnModel>();
        List<ColumnModel> columnModels = new ArrayList<ColumnModel>();

        for (Object object : batch.getColumns()) {
            columnModels.add(tableMapping.getColumnByJavaName(String.valueOf(object)));
        }
        for (Object object : batch.getWhereColumns()) {
            whereColumnModels.add(tableMapping.getColumnByJavaName(String.valueOf(object)));
        }

        Iterator<ColumnModel> cmIt = columnModels.iterator();
        StringBuilder set = new StringBuilder();
        Iterator<ColumnModel> wcIt = whereColumnModels.iterator();
        StringBuilder where = new StringBuilder();
        while (cmIt.hasNext()) {
            ColumnModel cm = cmIt.next();
            set.append(cm.getColumnName() + "=:" + cm.getJavaName());
            if (cmIt.hasNext()) {
                set.append(" AND ");
            }
        }

        while (wcIt.hasNext()) {
            ColumnModel cm = wcIt.next();
            where.append(cm.getColumnName() + "=:" + cm.getJavaName());
            if (wcIt.hasNext()) {
                where.append(" AND ");
            }
        }
        valuesForPrepared.setSql("UPDATE " + tableMapping.getDbTableName() + " SET " + set + " WHERE " + where);

        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    private ValuesForPrepared common(TableModel tableMapping, List<Wheres> wheres) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        StringBuilder sb = new StringBuilder();
        StringBuilder notWhereSql = new StringBuilder();
        List<ColumnModel> columnModels = tableMapping.getMappingPrimaryKeyColumnModels();
        Iterator<Wheres> wheresIterator = wheres.iterator();
        while (wheresIterator.hasNext()) {
            Wheres whs = wheresIterator.next();
            ColumnModel columnModel = tableMapping.getColumnByJavaName(whs.getKey());

            if (whs.isNormal()) {
                Wheres.Operation operation = whs.getOperation();
                if (operation.equals(Wheres.Operation.IN)) {
                    List<Object> valueList = (List<Object>) whs.getValue();

                    Iterator<Object> inIterator = valueList.iterator();
                    StringBuilder insb = new StringBuilder();
                    int i = 0;
                    while (inIterator.hasNext()) {
                        Object object = inIterator.next();
                        insb.append(":" + columnModel.getJavaName() + i);
                        valuesForPrepared.addValue(":" + columnModel.getJavaName() + i, object);
                        if (inIterator.hasNext()) {
                            insb.append(",");
                        }
                        i++;
                    }
                    sb.append(columnModel.getColumnName() + " IN(" + insb + ")");
                } else if (operation.equals(Wheres.Operation.LIKE)) {
                    sb.append(columnModel.getColumnName() + " LIKE(:" + columnModel.getJavaName() + ")");
                    valuesForPrepared.addValue(":" + columnModel.getJavaName(), whs.getValue());
                } else {
                    sb.append(columnModel.getColumnName() + whs.getOperation() + ":" + columnModel.getJavaName());
                    valuesForPrepared.addValue(":" + columnModel.getJavaName(), whs.getValue());
                }
                sb.append(" AND ");
            } else {
                if (whs.getType() == 1) {
                    if (columnModels.size() > 0) {
                        ColumnModel cm = columnModels.get(0);
                        sb.append(cm.getColumnName() + "=:" + cm.getJavaName());
                        valuesForPrepared.addValue(":" + cm.getJavaName(), whs.getValue());
                    }
                    sb.append(" AND ");
                } else if (whs.getType() == 2) {
                    notWhereSql.append(" LIMIT " + whs.getValue() + ",");
                } else if (whs.getType() == 3) {
                    notWhereSql.append(whs.getValue());
                } else if (whs.getType() == 4) {
                    int v = (Integer) whs.getValue();
                    notWhereSql.append(" ORDER BY " + columnModel.getColumnName() + " " + (v == Wheres.ORDER_ASC ? "ASC" : "DESC"));
                }
            }
        }
        if (sb.toString().endsWith(" AND ")) {
            valuesForPrepared.setSql(sb.toString().substring(0, sb.toString().length() - " AND ".length()) + notWhereSql.toString());
        } else {
            valuesForPrepared.setSql(sb.toString() + " " + notWhereSql.toString());
        }
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareDelete(TableModel tableMapping, List<Wheres> wheres) {
        ValuesForPrepared valuesForPrepared = common(tableMapping, wheres);
        String lastSQLString = valuesForPrepared.getSql();
        if (StringUtils.isBlank(lastSQLString)) {
            throw new SQLDialectException("delete sql must has where");
        }
        valuesForPrepared.setSql("DELETE FROM " + tableMapping.getDbTableName() + " WHERE " + valuesForPrepared.getSql());

        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareSelect(TableModel tableMapping, List<Wheres> wheres) {
        ValuesForPrepared valuesForPrepared = common(tableMapping, wheres);
        String lastSQLString = valuesForPrepared.getSql();
        valuesForPrepared.setSql("SELECT * FROM " + tableMapping.getDbTableName() + (StringUtils.isBlank(lastSQLString) ? "" : " WHERE " + valuesForPrepared.getSql()));

        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareSelectCount(TableModel tableMapping, List<Wheres> wheres) {
        ValuesForPrepared valuesForPrepared = common(tableMapping, wheres);
        String lastSQLString = valuesForPrepared.getSql();
        valuesForPrepared.setSql("SELECT COUNT(*) FROM " + tableMapping.getDbTableName() + (StringUtils.isBlank(lastSQLString) ? "" : " WHERE " + valuesForPrepared.getSql()));

        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    protected void showSQL(String sql) {
        if (this.isShowSQL) {
            logger.info(sql);
        }
    }

    @Override
    public void setShowSQL(boolean isShowSQL) {
        this.isShowSQL = isShowSQL;
    }
}
