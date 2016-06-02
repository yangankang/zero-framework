package com.yoosal.orm;

import com.yoosal.json.JSONObject;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

/**
 * 用来做添删改查的对象，继承自JSONObject，包含各种和数据库相关的信息
 */
public class ModelObject extends JSONObject {
    private Class<Enum> objectClass;
    private String dataSourceName;

    public static ModelObject instance(Class<Enum> clazz) {
        return new ModelObject(clazz);
    }

    public static ModelObject instance(Class<Enum> clazz, String dataSourceName) {
        return new ModelObject(clazz, dataSourceName);
    }

    public ModelObject(Class<Enum> objectClass) {
        this.objectClass = objectClass;
    }

    public ModelObject(Class<Enum> objectClass, String dataSourceName) {
        this.objectClass = objectClass;
        this.dataSourceName = dataSourceName;
    }

    public ModelObject() {
    }

    public void setObjectClass(Class<Enum> objectClass) {
        this.objectClass = objectClass;
    }

    public Class<Enum> getObjectClass() {
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
}
