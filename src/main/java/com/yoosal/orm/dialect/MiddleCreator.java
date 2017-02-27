package com.yoosal.orm.dialect;

import com.yoosal.common.Logger;
import com.yoosal.common.StringUtils;
import com.yoosal.json.JSONArray;
import com.yoosal.json.JSONObject;
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
        typesMapping.put(Date.class, "DATETIME");
        typesMapping.put(java.sql.Date.class, "DATETIME");
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
        SQLChain chain = new SQLChain(this.getEnumType());
        chain.alter().table().setValue(tableModel.getDbTableName());
        for (ColumnModel cm : existColumns) {
            chain.add().matchColumn(cm, this, false, true, true);
        }
        chain.removeLastCommand();
        String sql = chain.toString();
        showSQL(sql);
        return sql;
    }

    @Override
    public String createTable(TableModel tableModel) {
        List<ColumnModel> columnModelList = tableModel.getMappingColumnModels();

        SQLChain chain = new SQLChain(this.getEnumType());
        chain.create().table().ifCommand().not().exists().setValue(tableModel.getDbTableName()).setBegin();
        List<String> pk = new ArrayList<String>();
        List<String> key = new ArrayList<String>();
        List<String> index = new ArrayList<String>();
        for (ColumnModel cm : columnModelList) {
            chain.matchColumn(cm, this, false, false, false);
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

        SQLChain chain = new SQLChain(this.getEnumType());
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

        showSQL(valuesForPrepared.getSql(), valuesForPrepared);
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

        for (ColumnModel cm : tableMapping.getMappingColumnModels()) {
            if (object.containsKey(cm.getJavaName())) {
                if (whereColumns != null) {
                    if (!cm.isPrimaryKey()) {
                        columnModels.add(cm);
                    } else {
                        whereColumns.add(cm);
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
        List<ColumnModel> whereColumnModels = new ArrayList<ColumnModel>();
        List<ColumnModel> columnModels = getValidateColumn(tableMapping, object, whereColumnModels);

        return prepareUpdate(tableMapping, columnModels, whereColumnModels, object, object);
    }

    private ValuesForPrepared prepareUpdate(TableModel tableMapping, List<ColumnModel> columnModels,
                                            List<ColumnModel> whereColumnModels, ModelObject editor, ModelObject criteria) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();

        if (whereColumnModels.size() <= 0) {
            logger.debug("update sql no where statement");
            throw new IllegalArgumentException("更新操作不允许更新全表(没有对象id或者其他条件)！");
        }

        SQLChain chain = new SQLChain(this.getEnumType());
        chain.update().setValue(tableMapping.getDbTableName()).set();

        Iterator<ColumnModel> cmIt = columnModels.iterator();
        Iterator<ColumnModel> wcIt = whereColumnModels.iterator();
        while (cmIt.hasNext()) {
            ColumnModel cm = cmIt.next();
            valuesForPrepared.addValue(":" + cm.getJavaName(), editor.get(cm.getJavaName()));
            chain.setValue(cm.getColumnName()).setEquals().setValue(":" + cm.getJavaName());
            if (cmIt.hasNext()) {
                chain.setSplit();
            }
        }

        if (wcIt.hasNext()) {
            chain.where();
            while (wcIt.hasNext()) {
                ColumnModel cm = wcIt.next();
                valuesForPrepared.addValue(":" + cm.getJavaName(), criteria.get(cm.getJavaName()));
                chain.setValue(cm.getColumnName()).setEquals().setValue(":" + cm.getJavaName());
                if (wcIt.hasNext()) {
                    chain.and();
                }
            }
        }

        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql(), valuesForPrepared);
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareUpdate(TableModel tableModel, ModelObject editor, ModelObject criteria) {
        SQLChain chain = new SQLChain(this.getEnumType());
        chain.update().setValue(tableModel.getDbTableName()).set();

        List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
        List<ColumnModel> whereColumnModels = new ArrayList<ColumnModel>();
        for (ColumnModel cm : tableModel.getMappingColumnModels()) {
            if (editor.containsKey(cm.getJavaName())) {
                columnModels.add(cm);
            }
        }

        for (ColumnModel cm : tableModel.getMappingColumnModels()) {
            if (criteria.containsKey(cm.getJavaName())) {
                whereColumnModels.add(cm);
            }
        }

        return prepareUpdate(tableModel, columnModels, whereColumnModels, editor, criteria);
    }

    protected void showSQL(String sql) {
        if (this.isShowSQL) {
            logger.info(sql);
        }
    }

    protected void showSQL(String sql, ValuesForPrepared valuesForPrepared) {
        if (this.isShowSQL) {
            try {
                logger.info(sql);
                logger.info("SQL参数列表:" + JSONObject.toJSONString(valuesForPrepared.getKeyValues()));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        SQLChain chain = new SQLChain(this.getEnumType());
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
        showSQL(valuesForPrepared.getSql(), valuesForPrepared);

        return valuesForPrepared;
    }


    private void addIdToWheres(TableModel tableMapping, Object idValue, List<Wheres> wheres) {
        if (idValue != null) {
            List<ColumnModel> columnModels = tableMapping.getMappingPrimaryKeyColumnModels();
            if (columnModels.size() > 0) {
                ColumnModel cm = columnModels.get(0);
                wheres.add(new Wheres(cm.getJavaName(), idValue));
            }
        }
    }

    private void selectWhereChain(TableModel tableMapping, String tableAsName, Wheres whs, SQLChain chain, ValuesForPrepared valuesForPrepared) {
        ColumnModel columnModel = tableMapping.getColumnByJavaName(whs.getKey());

        String cn = columnModel.getColumnName();
        if (StringUtils.isNotBlank(tableAsName)) {
            cn = tableAsName + "." + cn;
        }
        Wheres.Operation operation = whs.getEnumOperation();
        if (operation.equals(Wheres.Operation.IN)) {
            List<Object> valueList = (List<Object>) whs.getValue();

            chain.setValue(cn).in().setBegin();
            Iterator<Object> inIterator = valueList.iterator();
            int i = 0;
            while (inIterator.hasNext()) {
                Object object = inIterator.next();
                chain.setValue(":" + cn + i);
                valuesForPrepared.addValue(":" + cn + i, object);
                if (inIterator.hasNext()) {
                    chain.setSplit();
                }
                i++;
            }
            chain.setEnd();
        } else if (operation.equals(Wheres.Operation.LIKE)) {
            chain.setValue(cn).like().setBegin().setValue(":" + cn).setEnd();
            valuesForPrepared.addValue(":" + cn, "%" + whs.getValue() + "%");
        } else if (operation.equals(Wheres.Operation.INTERVAL)) {
            if (whs.getIntervalStartValue() != null) {

                chain.setValue(cn + Wheres.getOperation(whs.getIntervalStartOperation()) + ":" + cn + "_start");
                valuesForPrepared.addValue(":" + cn + "_start", whs.getIntervalStartValue());
            }
            if (whs.getIntervalEndValue() != null) {
                if (whs.getIntervalStartValue() != null) {
                    chain.and();
                }
                chain.setValue(cn + Wheres.getOperation(whs.getIntervalEndOperation()) + ":" + cn + "_end");
                valuesForPrepared.addValue(":" + cn + "_end", whs.getIntervalEndValue());
            }
        } else {
            chain.setValue(cn + whs.getOperation() + ":" + cn);
            valuesForPrepared.addValue(":" + cn, whs.getValue());
        }
    }

    public abstract void setLimit(SQLChain chain, Limit limit);

    @Override
    public ValuesForPrepared prepareDelete(DBMapping tableMapping, Query query) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        SQLChain chain = new SQLChain(this.getEnumType());
        CreatorJoinModel joinModel = this.getJoinModel(tableMapping, query, false);
        List<CreatorJoinModel> allJoinModel = joinModel.getSelectColumns();

        List<Wheres> wheres = query.getWheres();
        this.addIdToWheres(joinModel.getTableModel(), query.getIdValue(), wheres);
        if (wheres == null || wheres.size() <= 0) {
            throw new SQLDialectException("delete sql must has where");
        }


        chain.delete();
        Iterator<CreatorJoinModel> iterator = allJoinModel.iterator();
        SQLChain tableSQLChain = new SQLChain();
        while (iterator.hasNext()) {
            CreatorJoinModel jm = iterator.next();
            chain.setValue(jm.getTableAsName());
            tableSQLChain.setValue(jm.getTableModel().getDbTableName()).as().setValue(jm.getTableAsName());
            if (iterator.hasNext()) {
                chain.setSplit();
                tableSQLChain.setSplit();
            }
        }
        chain.from();
        chain.setChain(tableSQLChain);

        this.setWheres(joinModel.getTableModel(), wheres, joinModel.getTableAsName(), chain, valuesForPrepared);

        this.setJoinWheres(tableMapping, joinModel, chain, valuesForPrepared, true);

        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql(), valuesForPrepared);
        return valuesForPrepared;
    }

    @Override
    public ValuesForPrepared prepareSelect(DBMapping tableMapping, Query query) {
        CreatorJoinModel joinModel = this.getJoinModel(tableMapping, query, true);
        List<CreatorJoinModel> allJoinModel = joinModel.getSelectColumns();
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        valuesForPrepared.setModel(joinModel);

        SQLChain chain = new SQLChain(this.getEnumType());
        chain.select();
        if (allJoinModel != null) {
            Iterator<CreatorJoinModel> iterator = allJoinModel.iterator();
            while (iterator.hasNext()) {
                CreatorJoinModel jm = iterator.next();
                Map<String, String> map = jm.getColumnAsName();
                String tname = jm.getTableAsName();
                Iterator mapIterator = map.entrySet().iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) mapIterator.next();
                    chain.setValue(tname + "." + entry.getKey()).as().setValue(entry.getValue());
                    if (mapIterator.hasNext()) {
                        chain.setSplit();
                    }
                }
                if (iterator.hasNext()) {
                    chain.setSplit();
                }
            }
        }

        chain.from().setValue(joinModel.getTableModel().getDbTableName()).as().setValue(joinModel.getTableAsName());

        this.setJoinWheres(tableMapping, joinModel, chain, valuesForPrepared, false);

        List<Wheres> wheres = query.getWheres();
        Object idValue = query.getIdValue();
        this.addIdToWheres(joinModel.getTableModel(), idValue, wheres);

        this.setWheres(joinModel.getTableModel(), wheres, joinModel.getTableAsName(), chain, valuesForPrepared);

        this.setLimitAndOrder(query, joinModel, chain);

        String sql = chain.toString();
        valuesForPrepared.setSql(sql);
        showSQL(valuesForPrepared.getSql(), valuesForPrepared);
        return valuesForPrepared;
    }

    private void setLimitAndOrder(Query query, CreatorJoinModel joinModel, SQLChain chain) {
        Limit limit = query.getLimit();
        OrderBy orderBy = query.getOrderBy();

        if (orderBy != null) {
            ColumnModel cm = joinModel.getTableModel().getColumnByJavaName(orderBy.getField());
            chain.order().by().setValue(joinModel.getTableAsName() + "." + cm.getColumnName()).setOrderBy(orderBy.getType());
        }
        if (limit != null) {
            this.setLimit(chain, limit);
        }
    }

    private void setJoinWheres(DBMapping tableMapping, CreatorJoinModel joinModel, SQLChain chain, ValuesForPrepared valuesForPrepared, boolean isDelete) {
        List<CreatorJoinModel> leftJoinModels = joinModel.getChild();
        if (leftJoinModels != null) {
            String valueColumn = "##";
            int valueCount = 0;
            Iterator<CreatorJoinModel> iterator = leftJoinModels.iterator();
            while (iterator.hasNext()) {
                CreatorJoinModel rightjm = iterator.next();
                Join join = rightjm.getJoin();
                if (!isDelete) {
                    chain.left().join().setValue(rightjm.getTableModel().getDbTableName()).as().setValue(rightjm.getTableAsName()).on();
                }
                List<Wheres> wheres = join.getWheres();
                for (int i = 0; i < wheres.size(); i++) {
                    Wheres wh = wheres.get(i);
                    Object whValue = wh.getValue();
                    if (i != 0 || isDelete) {
                        chain.setOperation(wh.getLogic());
                    }
                    String vc = valueColumn + (valueCount++);

                    Class leftTableClass = join.getSourceObjectClass() == null ? joinModel.getQuery().getObjectClass() : join.getSourceObjectClass();
                    TableModel leftm = tableMapping.getTableMapping(leftTableClass);
                    CreatorJoinModel leftjm = joinModel.getModelByTableModel(leftm);
                    ColumnModel leftcm = leftjm.getTableModel().getColumnByJavaName(wh.getKey());
                    ColumnModel rightcm = rightjm.getTableModel().getColumnByJavaName(whValue);


                    if (whValue.getClass().isEnum()) {
                        //where的value的enum字段类型不在cm这个表中
                        chain.setValue(leftjm.getTableAsName() + "." + leftcm.getColumnName())
                                .setValue(wh.getOperation())
                                .setValue(rightjm.getTableAsName() + "." + rightcm.getColumnName());
                    } else {
                        if (leftm == null) {
                            throw new SQLDialectException("current query not find table " + join.getSourceObjectClass().getSimpleName());
                        }
                        chain.setValue(joinModel.getModelByTableModel(leftm).getTableAsName() + "." + leftcm.getColumnName())
                                .setValue(wh.getOperation())
                                .setValue(":" + vc);
                    }

                    valuesForPrepared.addValue(":" + vc, whValue);
                }
            }
        }

    }

    private CreatorJoinModel getJoinModel(DBMapping dbMapping, Query query, boolean isColumn) {
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
        if (isColumn) {
            Map<String, String> columnAsName = new LinkedHashMap<String, String>();
            Map<String, String> javaColumnAsName = new LinkedHashMap<String, String>();
            for (ColumnModel cm : columnModels) {
                String an = c + (cint++);
                columnAsName.put(cm.getColumnName(), an);
                javaColumnAsName.put(cm.getJavaName(), an);
            }
            joinModel.setColumnAsName(columnAsName);
            joinModel.setJavaColumnAsName(javaColumnAsName);
        }
        joinModel.setTableAsName(t + (tint++));


        for (Join join : joins) {
            TableModel joinTableModel = dbMapping.getTableMapping(join.getObjectClass());
            List<ColumnModel> joinColumnModels = joinTableModel.getMappingColumnModels();
            CreatorJoinModel childJoinModel = new CreatorJoinModel();
            childJoinModel.setTableModel(joinTableModel);
            childJoinModel.setJoin(join);
            if (isColumn) {
                Map<String, String> childColumnAsName = new LinkedHashMap<String, String>();
                Map<String, String> javaChildColumnAsName = new LinkedHashMap<String, String>();
                for (ColumnModel cm : joinColumnModels) {
                    String an = c + (cint++);
                    childColumnAsName.put(cm.getColumnName(), an);
                    javaChildColumnAsName.put(cm.getJavaName(), an);
                }
                childJoinModel.setColumnAsName(childColumnAsName);
                childJoinModel.setJavaColumnAsName(javaChildColumnAsName);
            }
            childJoinModel.setTableAsName(t + (tint++));

            joinModel.addChild(childJoinModel);
        }


        return joinModel;
    }

    @Override
    public ValuesForPrepared prepareSelectCount(TableModel tableMapping, Query query) {
        ValuesForPrepared valuesForPrepared = new ValuesForPrepared();
        SQLChain chain = new SQLChain(this.getEnumType());
        chain.select().count().setBegin().setALL().setEnd().from().setValue(tableMapping.getDbTableName());

        List<Wheres> wheres = query.getWheres();
        if (wheres != null && wheres.size() > 0) {
            Object idValue = query.getIdValue();
            this.addIdToWheres(tableMapping, idValue, wheres);
            this.setWheres(tableMapping, wheres, null, chain, valuesForPrepared);
        }
        valuesForPrepared.setSql(chain.toString());
        showSQL(valuesForPrepared.getSql(), valuesForPrepared);
        return valuesForPrepared;
    }

    private void setWheres(TableModel tableModel, List<Wheres> wheres, String tableAsName, SQLChain chain, ValuesForPrepared valuesForPrepared) {

        if (wheres != null && wheres.size() > 0) {
            chain.where();
            for (int i = 0; i < wheres.size(); i++) {
                Wheres wh = wheres.get(i);

                List<Wheres.Priority> beginPriorities = wh.getBegins();
                List<Wheres.Priority> endPriorities = wh.getEnds();
                //添加开始的括号 (
                if (beginPriorities != null && beginPriorities.size() > 0) {
                    for (Wheres.Priority priority : beginPriorities) {
                        chain.setBegin();
                    }
                }

                if (i != 0) {
                    chain.setOperation(wh.getLogic());
                }

                selectWhereChain(tableModel, tableAsName, wh, chain, valuesForPrepared);

                //添加结束的括号 )
                if (endPriorities != null && endPriorities.size() > 0) {
                    for (Wheres.Priority priority : endPriorities) {
                        chain.setEnd();
                    }
                }
            }
        }
    }

    @Override
    public void setShowSQL(boolean isShowSQL) {
        this.isShowSQL = isShowSQL;
    }
}
