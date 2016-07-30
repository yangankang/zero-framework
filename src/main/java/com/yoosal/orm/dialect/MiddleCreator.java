package com.yoosal.orm.dialect;

import com.yoosal.common.Logger;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.ModelObject;
import com.yoosal.orm.core.Batch;
import com.yoosal.orm.exception.SQLDialectException;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.*;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

public abstract class MiddleCreator implements SQLDialect {
    private static final Logger logger = Logger.getLogger(MiddleCreator.class);
    protected boolean isShowSQL = false;
    protected static final Map<Integer, String> types = new HashMap<Integer, String>();
    protected static final Map<Class, String> typesMapping = new HashMap<Class, String>();

    static {
        typesMapping.put(String.class, "VARCHAR");
        typesMapping.put(char.class, "CHAR");
        typesMapping.put(Integer.class, "INT");
        typesMapping.put(int.class, "INT");
        typesMapping.put(Date.class, "TIMESTAMP");
        typesMapping.put(java.sql.Date.class, "TIMESTAMP");
        typesMapping.put(Short.class, "SMALLINT");
        typesMapping.put(short.class, "SMALLINT");
        typesMapping.put(Byte.class, "TINYINT");
        typesMapping.put(byte.class, "TINYINT");
        typesMapping.put(Long.class, "BIGINT");
        typesMapping.put(long.class, "BIGINT");
        typesMapping.put(Float.class, "FLOAT");
        typesMapping.put(float.class, "FLOAT");
    }

    static {
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
    public String getType(Class clazz) {
        return typesMapping.get(clazz);
    }

    @Override
    public String addColumn(TableModel tableModel, List<ColumnModel> existColumns) {
        SQLChain chain = new SQLChain();
        chain.alter().table().setValue(tableModel.getDbTableName());
        for (ColumnModel cm : existColumns) {
            chain.add().matchColumn(cm, this, false, true);
        }
        chain.removeLastCommand();
        String sql = chain.toString();
        showSQL(sql);
        return sql;
    }

    @Override
    public String createTable(TableModel tableModel) {
        List<ColumnModel> columnModelList = tableModel.getMappingColumnModels();

        SQLChain chain = new SQLChain();
        chain.create().table().ifCommand().not().exists().setValue(tableModel.getDbTableName()).setBegin();
        List<String> pk = new ArrayList<String>();
        List<String> key = new ArrayList<String>();
        List<String> index = new ArrayList<String>();
        for (ColumnModel cm : columnModelList) {
            chain.matchColumn(cm, this, false, false);
            if (cm.isPrimaryKey()) {
                pk.add(cm.getColumnName());
            }
            if (cm.isKey()) {
                key.add(cm.getColumnName());
            }
            if (cm.isIndex()) {
                index.add(cm.getColumnName());
            }
        }
        if (pk.size() > 0) {
            chain.primary().key().setBegin().setValueList(pk).setEnd().setSplit();
        }
        if (key.size() > 0) {
            chain.key().setBegin().setValueList(key).setEnd().setSplit();
        }
        if (index.size() > 0) {
            chain.index().setBegin().setValueList(index).setEnd().setSplit();
        }
        chain.removeLastCommand();
        chain.setEnd();
        setEngine(chain);

        String sql = chain.toString();
        showSQL(sql);
        return sql;
    }

    public abstract void setEngine(SQLChain chain);

    @Override
    public ValuesForPrepared prepareInsert(TableModel tableMapping, ModelObject object) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        List<ColumnModel> columnModels = getValidateColumn(tableMapping, object, null);

        SQLChain chain = new SQLChain();
        chain.insert().into().setValue(tableMapping.getDbTableName());

        List key = new ArrayList();
        List value = new ArrayList();
        Iterator<ColumnModel> it = columnModels.iterator();
        while (it.hasNext()) {
            ColumnModel cm = it.next();
            key.add(cm.getColumnName());
            value.add(":" + cm.getJavaName());
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
        }
        chain.setBegin().setValueList(key).setEnd();
        chain.values();
        chain.setBegin().setValueList(value).setEnd();
        String sql = chain.toString();
        valuesForPrepared.setSql(sql);

        showSQL(valuesForPrepared.getSql());
        return valuesForPrepared;
    }

    private boolean contains(Object[] wcs, ColumnModel cm) {
        if (wcs != null && cm != null) {
            for (Object object : wcs) {
                if (String.valueOf(object).equals(cm.getJavaName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<ColumnModel> getValidateColumn(TableModel tableMapping, ModelObject object, List<ColumnModel> whereColumns) {
        List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
        object.clearNull();
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
    public ValuesForPrepared prepareUpdate(TableModel tableMapping, ModelObject object) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        List<ColumnModel> whereColumnModels = new ArrayList<ColumnModel>();
        List<ColumnModel> columnModels = getValidateColumn(tableMapping, object, whereColumnModels);

        if (whereColumnModels.size() <= 0) {
            throw new SQLDialectException("update sql no where statement");
        }

        SQLChain chain = new SQLChain();
        chain.update().setValue(tableMapping.getDbTableName()).set();

        Iterator<ColumnModel> cmIt = columnModels.iterator();
        Iterator<ColumnModel> wcIt = whereColumnModels.iterator();
        while (cmIt.hasNext()) {
            ColumnModel cm = cmIt.next();
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
            chain.setValue(cm.getColumnName()).setEquals().setValue(":" + cm.getJavaName());
            if (cmIt.hasNext()) {
                chain.setSplit();
            }
        }

        chain.where();
        while (wcIt.hasNext()) {
            ColumnModel cm = wcIt.next();
            valuesForPrepared.addValue(":" + cm.getJavaName(), object.get(cm.getJavaName()));
            chain.setValue(cm.getColumnName()).setSplit().setValue(":" + cm.getJavaName());
            if (wcIt.hasNext()) {
                chain.and();
            }
        }

        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    protected void showSQL(String sql) {
        if (this.isShowSQL) {
            logger.info(sql);
        }
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
        SQLChain chain = new SQLChain();
        chain.update().setValue(tableMapping.getDbTableName()).set();

        Iterator<ColumnModel> cmIt = columnModels.iterator();
        Iterator<ColumnModel> wcIt = whereColumnModels.iterator();
        while (cmIt.hasNext()) {
            ColumnModel cm = cmIt.next();
            chain.setValue(cm.getColumnName()).setEquals().setValue(":" + cm.getJavaName());
            if (cmIt.hasNext()) {
                chain.setSplit();
            }
        }
        chain.where();
        while (wcIt.hasNext()) {
            ColumnModel cm = wcIt.next();
            chain.setValue(cm.getColumnName()).setEquals().setValue(":" + cm.getJavaName());
            if (wcIt.hasNext()) {
                chain.and();
            }
        }
        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql());

        return valuesForPrepared;
    }

    private ValuesForPrepared common(TableModel tableMapping, Query query, ValuesForPrepared valuesForPrepared, SQLChain chain) {
        List<Wheres> wheres = query.getWheres();
        if (wheres == null || wheres.size() == 0) {
            return valuesForPrepared;
        }
        chain.where();
        Object idValue = query.getIdValue();
        if (idValue != null) {
            List<ColumnModel> columnModels = tableMapping.getMappingPrimaryKeyColumnModels();
            if (columnModels.size() > 0) {
                ColumnModel cm = columnModels.get(0);
                wheres.add(new Wheres(cm.getJavaName(), idValue));
            }
        }
        Iterator<Wheres> wheresIterator = wheres.iterator();
        boolean isFirst = true;
        while (wheresIterator.hasNext()) {
            Wheres whs = wheresIterator.next();
            ColumnModel columnModel = tableMapping.getColumnByJavaName(whs.getKey());
            if (isFirst) {
                chain.setOperation(whs.getLogic());
            }
            isFirst = false;
            Wheres.Operation operation = whs.getEnumOperation();
            if (operation.equals(Wheres.Operation.IN)) {
                List<Object> valueList = (List<Object>) whs.getValue();

                chain.in();
                chain.setBegin();
                Iterator<Object> inIterator = valueList.iterator();
                int i = 0;
                while (inIterator.hasNext()) {
                    Object object = inIterator.next();
                    chain.setValue(":" + columnModel.getJavaName() + i);
                    valuesForPrepared.addValue(":" + columnModel.getJavaName() + i, object);
                    if (inIterator.hasNext()) {
                        chain.setSplit();
                    }
                    i++;
                }
                chain.setEnd();
            } else if (operation.equals(Wheres.Operation.LIKE)) {
                chain.setValue(columnModel.getColumnName()).like().setBegin().setValue(":" + columnModel.getJavaName()).setEnd();
                valuesForPrepared.addValue(":" + columnModel.getJavaName(), "%" + whs.getValue() + "%");
            } else {
                chain.setValue(columnModel.getColumnName() + whs.getOperation() + ":" + columnModel.getJavaName());
                valuesForPrepared.addValue(":" + columnModel.getJavaName(), whs.getValue());
            }
        }

        Limit limit = query.getLimit();
        OrderBy orderBy = query.getOrderBy();

        if (orderBy != null) {
            chain.order().by().setValue(orderBy.getField()).setOrderBy(orderBy.getType());
        }
        if (limit != null) {
            this.setLimit(chain, limit);
        }

        valuesForPrepared.setSql(chain.toString());
        return valuesForPrepared;
    }

    public abstract void setLimit(SQLChain chain, Limit limit);

    @Override
    public ValuesForPrepared prepareDelete(TableModel tableMapping, Query query) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        SQLChain chain = new SQLChain();
        chain.delete().from().setValue(tableMapping.getDbTableName());
        valuesForPrepared = common(tableMapping, query, valuesForPrepared, chain);
        String lastSQLString = valuesForPrepared.getSql();
        if (StringUtils.isBlank(lastSQLString)) {
            throw new SQLDialectException("delete sql must has where");
        }
        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql());
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareSelect(DBMapping tableMapping, Query query) {
        CreatorJoinModel joinModel = this.getJoinModel(tableMapping, query);
        List<CreatorJoinModel> allJoinModel = joinModel.getSelectColumns();
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        valuesForPrepared.setModel(joinModel);

        SQLChain chain = new SQLChain();
        chain.select();
        if (allJoinModel != null) {
            for (CreatorJoinModel jm : allJoinModel) {
                Map<String, String> map = jm.getColumnAsName();
                String tname = jm.getTableAsName();
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                    chain.setValue(tname + "." + entry.getKey()).as().setValue(entry.getValue());
                    if (iterator.hasNext()) {
                        chain.setSplit();
                    }
                }
            }
        }

        chain.from().setValue(joinModel.getTableModel().getDbTableName()).as().setValue(joinModel.getTableAsName());

        List<CreatorJoinModel> leftJoinModels = joinModel.getChild();
        String valueColumn = "##";
        int valueCount = 0;

        if (leftJoinModels != null) {
            for (CreatorJoinModel cm : leftJoinModels) {
                Join join = cm.getJoin();
                chain.left().join().setValue(cm.getTableModel().getDbTableName()).as().setValue(cm.getTableAsName()).on();
                List<Wheres> wheres = join.getWheres();
                for (int i = 0; i < wheres.size(); i++) {
                    Wheres wh = wheres.get(i);
                    Object whValue = wh.getValue();
                    if (i != 0) {
                        chain.setOperation(wh.getLogic());
                    }
                    if (whValue.getClass().isEnum()) {
                        chain.setValue(cm.getTableAsName() + "." + wh.getKey())
                                .setValue(wh.getOperation())
                                .setValue(cm.getTableAsName() + "." + wh.getValue());
                    } else {
                        TableModel leftm = tableMapping.getTableMapping(join.getSourceObjectClass());
                        if (leftm == null) {
                            throw new SQLDialectException("current query not find table " + join.getSourceObjectClass().getSimpleName());
                        }
                        String vc = valueColumn + (valueCount++);
                        chain.setValue(joinModel.getModelByTableModel(leftm) + "." + wh.getKey())
                                .setValue(wh.getOperation())
                                .setValue(":" + vc);
                        valuesForPrepared.addValue(":" + vc, whValue);
                    }
                }
            }
        }

        List<Wheres> wheres = query.getWheres();
        if (wheres != null && wheres.size() > 0) {
            chain.where();
            for (int i = 0; i < wheres.size(); i++) {
                Wheres wh = wheres.get(i);
                if (i != 0) {
                    chain.setOperation(wh.getLogic());
                }
                String k = joinModel.getTableAsName() + "." + wh.getKey();
                chain.setValue(k)
                        .setValue(wh.getOperation())
                        .setValue(":" + k);

                valuesForPrepared.addValue(":" + k, wh.getValue());
            }
        }

        String sql = chain.toString();
        valuesForPrepared.setSql(sql);
        showSQL(valuesForPrepared.getSql());
        return valuesForPrepared;
    }

    private CreatorJoinModel getJoinModel(DBMapping dbMapping, Query query) {
        TableModel tableModel = dbMapping.getTableMapping(query.getObjectClass());
        List<ColumnModel> columnModels = tableModel.getMappingColumnModels();
        List<Join> joins = query.getJoins();

        String t = "t";
        int tint = 0;
        String c = "c";
        int cint = 0;

        CreatorJoinModel joinModel = new CreatorJoinModel();
        joinModel.setTableModel(tableModel);
        joinModel.setQuery(query);
        Map<String, String> columnAsName = new LinkedHashMap<String, String>();
        Map<String, String> javaColumnAsName = new LinkedHashMap<String, String>();
        for (ColumnModel cm : columnModels) {
            String an = c + (cint++);
            columnAsName.put(cm.getColumnName(), an);
            javaColumnAsName.put(cm.getJavaName(), an);
        }
        joinModel.setColumnAsName(columnAsName);
        joinModel.setJavaColumnAsName(javaColumnAsName);
        joinModel.setTableAsName(t + (tint++));


        for (Join join : joins) {
            TableModel joinTableModel = dbMapping.getTableMapping(join.getObjectClass());
            List<ColumnModel> joinColumnModels = tableModel.getMappingColumnModels();
            CreatorJoinModel childJoinModel = new CreatorJoinModel();
            childJoinModel.setTableModel(joinTableModel);
            childJoinModel.setJoin(join);
            Map<String, String> childColumnAsName = new LinkedHashMap<String, String>();
            Map<String, String> javaChildColumnAsName = new LinkedHashMap<String, String>();
            for (ColumnModel cm : joinColumnModels) {
                String an = c + (cint++);
                childColumnAsName.put(cm.getColumnName(), an);
                javaChildColumnAsName.put(cm.getJavaName(), an);
            }
            childJoinModel.setColumnAsName(childColumnAsName);
            childJoinModel.setJavaColumnAsName(javaChildColumnAsName);
            childJoinModel.setTableAsName(t + (tint++));

            joinModel.addChild(childJoinModel);
        }


        return joinModel;
    }

    @Override
    public ValuesForPrepared prepareSelectCount(TableModel tableMapping, Query query) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        SQLChain chain = new SQLChain();
        chain.select().count().setBegin().setALL().setEnd().from().setValue(tableMapping.getDbTableName());
        valuesForPrepared = common(tableMapping, query, valuesForPrepared, chain);
        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql());
        return valuesForPrepared;
    }

    @Override
    public void setShowSQL(boolean isShowSQL) {
        this.isShowSQL = isShowSQL;
    }
}
