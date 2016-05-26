package com.yoosal.orm;

import com.yoosal.json.JSONObject;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

/**
 * 用来做添删改查的对象，继承自JSONObject，包含各种和数据库相关的信息
 */
public class ModelObject extends JSONObject {
    private Class<Enum> clazz;
    private String dataSourceName;

    public static ModelObject instance(Class<Enum> clazz) {
        return new ModelObject(clazz);
    }

    public static ModelObject instance(Class<Enum> clazz, String dataSourceName) {
        return new ModelObject(clazz, dataSourceName);
    }

    public ModelObject(Class<Enum> clazz) {
        this.clazz = clazz;
    }

    public ModelObject(Class<Enum> clazz, String dataSourceName) {
        this.clazz = clazz;
        this.dataSourceName = dataSourceName;
    }

    public ModelObject() {
    }

    public void setClazz(Class<Enum> clazz) {
        this.clazz = clazz;
    }

    public ModelObject clone() {
        return new ModelObject(clazz, dataSourceName);
    }

    public Query getQuery() {
        return new Query(clazz, dataSourceName);
    }

    public Join getJoin() {
        return new Join(clazz, dataSourceName);
    }
}
