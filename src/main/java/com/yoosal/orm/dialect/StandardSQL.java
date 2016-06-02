package com.yoosal.orm.dialect;

import com.yoosal.common.CollectionUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.core.Batch;
import com.yoosal.orm.exception.SQLDialectException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Wheres;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

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

    private String keyString(List<ColumnModel> columnModels) {
        List<String> strings = new ArrayList<String>();
        for (ColumnModel cm : columnModels) {
            strings.add(cm.getColumnName());
        }
        return StringUtils.collectionToDelimitedString(strings, ",");
    }

    private String valueString(List<ColumnModel> columnModels) {
        Iterator it = columnModels.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            sb.append("?");
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String valueUpdateString(List<ColumnModel> columnModels) {
        Iterator<ColumnModel> it = columnModels.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            ColumnModel cm = it.next();
            sb.append(cm.getColumnName() + "=?");
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String valueUpdateWhereString(List<ColumnModel> columnModels) {
        Iterator<ColumnModel> it = columnModels.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            ColumnModel cm = it.next();
            sb.append(cm.getColumnName() + "=?");
            if (it.hasNext()) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
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
                        if (cm.getIsPrimaryKey() <= 0) {
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
        valuesForPrepared.setKeys((ColumnModel[]) columnModels.toArray());
        valuesForPrepared.setSql("INSERT INTO " + tableMapping.getDbTableName() + " (" + keyString(columnModels) + ") VALUES ("
                + valueString(columnModels) + ")");
        valuesForPrepared.setCount(columnModels.size());
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

        ColumnModel[] array1 = (ColumnModel[]) columnModels.toArray();
        ColumnModel[] array2 = (ColumnModel[]) whereColumnModels.toArray();
        ColumnModel[] cms = new ColumnModel[array1.length + array2.length];
        System.arraycopy(array1, 0, cms, 0, array1.length);
        System.arraycopy(array2, 0, cms, array1.length, array2.length);

        valuesForPrepared.setKeys(cms);
        valuesForPrepared.setSql("UPDATE " + tableMapping.getDbTableName() +
                " SET " + valueUpdateString(columnModels) +
                " WHERE " + valueUpdateWhereString(whereColumnModels));
        valuesForPrepared.setCount(columnModels.size());
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareUpdateBatch(TableModel tableMapping, Batch batch) {
        Object[] objects = batch.getColumns();
        Object[] whereColumns = batch.getWhereColumns();
        List<Object> objectList = Arrays.asList(objects);
        Iterator<Object> it = objectList.iterator();
        StringBuffer set = new StringBuffer();

        List<ColumnModel> columnModels = tableMapping.getMappingColumnModels();
        List<ColumnModel> cms = new ArrayList<ColumnModel>();
        Map<String, ColumnModel> toMap = new HashMap<String, ColumnModel>();
        for (ColumnModel cm : columnModels) {
            toMap.put(cm.getJavaName(), cm);
        }
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            set.append(key + "=?");
            if (it.hasNext()) {
                set.append(",");
            }
            cms.add(toMap.get(key));
        }

        Iterator<Object> whereIt = Arrays.asList(whereColumns).iterator();
        StringBuffer where = new StringBuffer();
        while (whereIt.hasNext()) {
            String key = String.valueOf(whereIt.next());
            where.append(key + "=?");
            if (whereIt.hasNext()) {
                where.append(" AND ");
            }
            cms.add(toMap.get(key));
        }

        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        valuesForPrepared.setSql("UPDATE " + tableMapping.getDbTableName() + " SET " + set.toString() + " WHERE " + where.toString());
        valuesForPrepared.setKeys((ColumnModel[]) cms.toArray());
        valuesForPrepared.setCount(cms.size());

        return valuesForPrepared;
    }

    private String valueWheresString(List<Wheres> wheres) {
        Iterator<Wheres> it = wheres.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            Wheres where = it.next();
            sb.append(where.getKey() + "=?");
            if (it.hasNext()) {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }

    private String keyWhereString(List<Wheres> wheres) {
        List<String> strings = new ArrayList<String>();
        for (Wheres where : wheres) {
            strings.add(where.getKey());
        }
        return StringUtils.collectionToDelimitedString(strings, ",");
    }

    private String queWhereString(List<Wheres> wheres) {
        Iterator it = wheres.iterator();
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            sb.append("?");
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public ValuesForPrepared prepareDelete(TableModel tableMapping, List<Wheres> wheres) {
        
        return null;
    }

    @Override
    public ValuesForPrepared prepareSelect(TableModel tableMapping, List<Wheres> wheres) {
        return null;
    }

    @Override
    public ValuesForPrepared prepareSelectCount(TableModel tableMapping, List<Wheres> wheres) {
        return null;
    }
}
