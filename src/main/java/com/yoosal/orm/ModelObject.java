package com.yoosal.orm;

import com.yoosal.common.StringUtils;
import com.yoosal.json.JSON;
import com.yoosal.json.JSONObject;
import com.yoosal.mvc.convert.ConversionService;
import com.yoosal.mvc.convert.service.DefaultConversionService;
import com.yoosal.orm.core.Check;
import com.yoosal.orm.core.CheckFactory;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用来做添删改查的对象，继承自JSONObject，包含各种和数据库相关的信息
 */
public class ModelObject extends JSONObject {
    private static final ConversionService conversionService = new DefaultConversionService();
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

    /**
     * 为了能够链式编程
     *
     * @param m
     * @return
     */
    @Override
    public ModelObject fluentPutAll(Map<? extends Object, ? extends Object> m) {
        super.fluentPutAll(m);
        return this;
    }

    /**
     * 把sourceObject中的字段key的值，添加到当前ModelObject对象中
     *
     * @param key
     * @param sourceObject
     */
    public void copy(Object key, ModelObject sourceObject) {
        this.put(key, sourceObject.get(key));
    }

    /**
     * 字符串转换成为ModelObject对象
     *
     * @param text
     * @return
     */
    public static ModelObject parseObject(String text) {
        Object obj = parse(text);
        ModelObject object = null;
        JSONObject json = null;
        if (obj instanceof JSONObject) {
            json = (JSONObject) obj;
        } else {
            json = (JSONObject) JSON.toJSON(obj);
        }

        if (json != null) {
            object = new ModelObject();
            for (Map.Entry entry : json.entrySet()) {
                object.put(entry.getKey(), entry.getValue());
            }
        }
        return object;
    }

    /**
     * 清除包含null的字段
     */
    public void clearNull() {
        List keys = new ArrayList();
        for (Map.Entry entry : this.entrySet()) {
            Object object = entry.getValue();
            if (object == null) {
                keys.add(entry.getKey());
            }
        }
        for (Object key : keys) {
            this.remove(key);
        }
    }

    /**
     * 清除所有无效字段，为空的包括null 和 ""
     */
    public void clearEmpty() {
        List keys = new ArrayList();
        for (Map.Entry entry : this.entrySet()) {
            Object object = entry.getValue();
            if (object == null) {
                keys.add(entry.getKey());
            } else if (object instanceof String && StringUtils.isBlank(String.valueOf(object))) {
                keys.add(entry.getKey());
            }
        }
        for (Object key : keys) {
            this.remove(key);
        }
    }

    /**
     * 把字段的值转变类型
     *
     * @param key
     * @param targetClass
     */
    public void convert(Object key, Class targetClass) throws Exception {
        Object value = this.get(key);
        if (value != null) {
            try {
                value = conversionService.executeConversion(value, targetClass);
                this.put(key, value);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * 校验当前对象中key的值是否正确，返回true和false
     *
     * @param key
     * @param c
     * @return
     */
    public boolean check(Object key, Class<? extends Check> c) {
        Check check = CheckFactory.getCheck(c);
        if (check != null) {
            return check.check(this.get(key));
        }
        return false;
    }


    /**
     * 校验当前对象中key的值是否正确，返回一个code可根据不同的code写不同的逻辑
     *
     * @param key
     * @param c
     * @return
     */
    public int checkForCode(Object key, Class<? extends Check> c) {
        Check check = CheckFactory.getCheck(c);
        if (check != null) {
            return check.verify(this.get(key));
        }
        return -1;
    }

    /**
     * 校验当前对象中key的值是否正确，不正确会抛出异常，适用于只校验不反馈的业务逻辑
     *
     * @param key
     * @param c
     * @return
     */
    public void checkAndThrow(Object key, Class<? extends Check> c) {
        if (!check(key, c)) {
            throw new IllegalArgumentException("failure check " + key + " value:" + this.get(key));
        }
    }

    public boolean isEmpty(Object key) {
        if (this.get(key) == null || StringUtils.isBlank((String) this.get(key))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNotEmpty(Object key) {
        if (!isEmpty(key)) {
            return true;
        } else {
            return false;
        }
    }
}
