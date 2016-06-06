package com.yoosal.orm;

import com.yoosal.json.JSONObject;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

import java.util.Map;

/**
 * 用来做添删改查的对象，继承自JSONObject，包含各种和数据库相关的信息
 */
public class ModelObject extends JSONObject {
    private Class objectClass;
    private String dataSourceName;
    /**
     * 单条更新时，这个数组表示当前列不作为where条件而是update的set需要的列，一般更新一个对象会以主键作为
     * 更新where条件，但是如果有多列主键需要更新其中一列或者多列，则用此数组排除
     */
    private Object[] updateColumn;
    /**
     * 单条更新时，表示当前对象更新时需要的where条件的字段列表
     */
    private Object[] whereColumn;

    public static ModelObject instance(Class clazz) {
        return new ModelObject(clazz);
    }

    public static ModelObject instance(Class clazz, String dataSourceName) {
        return new ModelObject(clazz, dataSourceName);
    }

    public ModelObject(Class objectClass) {
        this.objectClass = objectClass;
    }

    public ModelObject(Class objectClass, String dataSourceName) {
        this.objectClass = objectClass;
        this.dataSourceName = dataSourceName;
    }

    public ModelObject() {
    }

    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public ModelObject clone() {
        return new ModelObject(objectClass, dataSourceName);
    }

    public Query getQuery() {
        return new Query(objectClass, dataSourceName);
    }

    public Join getJoin() {
        return new Join(objectClass, dataSourceName);
    }

    public Object[] getUpdateColumn() {
        return updateColumn;
    }

    public void setUpdateColumn(Object... updateColumn) {
        this.updateColumn = updateColumn;
    }

    public Object[] getWhereColumn() {
        return whereColumn;
    }

    public void setWhereColumn(Object... whereColumn) {
        this.whereColumn = whereColumn;
    }

    @Override
    public ModelObject fluentPut(Object key, Object value) {
        super.fluentPut(key, value);
        return this;
    }

    @Override
    public ModelObject fluentPutAll(Map<? extends Object, ? extends Object> m) {
        super.fluentPutAll(m);
        return this;
    }
}
