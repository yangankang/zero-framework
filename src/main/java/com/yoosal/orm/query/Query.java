package com.yoosal.orm.query;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件，Class是必须的，用来指定表和其他映射操作，dataSourceName非必须的这个用来
 * 判断当前的查询是在哪个数据源上
 */
public class Query {
    private Class<Enum> clazz;
    private String dataSourceName;
    private List<Wheres> wheres = new ArrayList<Wheres>();
    private List<Wheres> limitWheres = new ArrayList<Wheres>();
    private List<Wheres> orderByWheres = new ArrayList<Wheres>();
    private List<Join> joins = new ArrayList<Join>();

    public static Query query(Class clazz) {
        return new Query(clazz);
    }

    public static Query where(Class clazz, Object key, Object value) {
        Query query = new Query(clazz);
        query.where(key, value);
        return query;
    }

    public static Query where(Class clazz, Object key, Object value, Wheres.Operation operation) {
        Query query = new Query(clazz);
        query.where(key, value, operation);
        return query;
    }

    public Query(Class clazz) {
        this.clazz = clazz;
    }

    public Query(Class clazz, String dataSourceName) {
        this.clazz = clazz;
        this.dataSourceName = dataSourceName;
    }

    public Query setClazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public Query setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    /**
     * 通过主键查询，如果有复合主键则传入多个主键的值，
     * 主键的顺序是根据Column注解中配置的key的值决定的
     *
     * @param value
     * @return
     */
    public Query id(Object value) {
        this.wheres.add(new Wheres(null, value, Wheres.TYPE_ID));
        return this;
    }

    /**
     * 添加一个查询条件
     *
     * @param wheres
     * @return
     */
    public Query where(Wheres wheres) {
        this.wheres.add(wheres);
        return this;
    }

    public Query where(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value));
        return this;
    }

    public Query where(Object key, Object value, Wheres.Operation operation) {
        this.wheres.add(new Wheres(String.valueOf(key), value, operation));
        return this;
    }

    public Query limit(long start, long limit) {
        this.limitWheres.add(new Wheres(null, start, Wheres.TYPE_START));
        this.limitWheres.add(new Wheres(null, limit, Wheres.TYPE_LIMIT));
        return this;
    }

    public Query orderByAsc(Object key) {
        this.orderByWheres.add(new Wheres(String.valueOf(key), Wheres.Order.asc, Wheres.TYPE_ORDER));
        return this;
    }

    public Query orderByDesc(Object key) {
        this.orderByWheres.add(new Wheres(String.valueOf(key), Wheres.Order.desc, Wheres.TYPE_ORDER));
        return this;
    }

    public Query in(Object key, List<Object> values) {
        this.wheres.add(new Wheres(String.valueOf(key), values, Wheres.Operation.IN));
        return this;
    }

    public Query or(Object key, List<Object> values) {
        this.wheres.add(new Wheres(String.valueOf(key), values, Wheres.Operation.OR));
        return this;
    }

    public Query like(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value, Wheres.Operation.LIKE));
        return this;
    }

    public Query join(Join join) {
        joins.add(join);
        return this;
    }

    public List<Join> getJoins() {
        return joins;
    }

    /**
     * 获得条件会产生一个新的List因为条件会有顺序问题，如果顺序不正确则产生的SQL也不正确
     *
     * @return
     */
    public List<Wheres> getWheres() {
        List<Wheres> whs = new ArrayList<Wheres>();
        whs.addAll(wheres);
        whs.addAll(orderByWheres);
        whs.addAll(limitWheres);
        return whs;
    }

    public Class getObjectClass() {
        return clazz;
    }
}
