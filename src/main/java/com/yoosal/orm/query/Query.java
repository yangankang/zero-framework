package com.yoosal.orm.query;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件，Class是必须的，用来指定表和其他映射操作，dataSourceName非必须的这个用来
 * 判断当前的查询是在哪个数据源上
 */
public class Query {
    private Class clazz;
    private String dataSourceName;
    private List<Wheres> wheres = new ArrayList<Wheres>();
    private Limit limit = null;
    private OrderBy orderBy = null;
    private Object idValue = null;
    private List<Join> joins = new ArrayList<Join>();
    private boolean isMaster = true;

    public static Query query(Class clazz) {
        return new Query(clazz);
    }

    public static Query and(Class clazz, Object key, Object value) {
        Query query = new Query(clazz);
        query.and(key, value);
        return query;
    }

    public static Query and(Class clazz, Object key, Object value, Wheres.Operation operation) {
        Query query = new Query(clazz);
        query.and(key, value, operation);
        return query;
    }

    public static Query or(Class clazz, Object key, Object value) {
        Query query = new Query(clazz);
        query.or(key, value);
        return query;
    }

    public static Query or(Class clazz, Object key, Object value, Wheres.Operation operation) {
        Query query = new Query(clazz);
        query.or(key, value, operation);
        return query;
    }


    public static Query priority(Class clazz, Wheres... wheres) {
        Query query = new Query(clazz);
        if (wheres.length > 1) {
            wheres[0].addBeginPriority();
            wheres[wheres.length - 1].addEndPriority();
        }
        for (Wheres wh : wheres) {
            query.where(wh);
        }
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

    public Query master() {
        this.isMaster = true;
        return this;
    }

    public Query slave() {
        this.isMaster = false;
        return this;
    }

    public boolean isMaster() {
        return this.isMaster;
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
        this.idValue = value;
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

    public Query and(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value, Wheres.Logic.AND));
        return this;
    }

    public Query and(Object key, Object value, Wheres.Operation operation) {
        this.wheres.add(new Wheres(String.valueOf(key), value, operation, Wheres.Logic.AND));
        return this;
    }

    public Query or(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value, Wheres.Logic.OR));
        return this;
    }

    public Query or(Object key, Object value, Wheres.Operation operation) {
        this.wheres.add(new Wheres(String.valueOf(key), value, operation, Wheres.Logic.OR));
        return this;
    }

    public Query limit(long start, long limit) {
        if (start > -1 && limit > -1) {
            if (this.limit == null) {
                this.limit = new Limit();
            }
            this.limit.setStart(start);
            this.limit.setLimit(limit);
        }
        return this;
    }

    public Query orderByAsc(Object key) {
        if (this.orderBy == null) {
            this.orderBy = new OrderBy();
        }
        this.orderBy.setType(OrderBy.Type.ASC);
        this.orderBy.setField(key);
        return this;
    }

    public Query orderByDesc(Object key) {
        if (this.orderBy == null) {
            this.orderBy = new OrderBy();
        }
        this.orderBy.setType(OrderBy.Type.DESC);
        this.orderBy.setField(key);
        return this;
    }

    public Query in(Object key, List<Object> values) {
        this.wheres.add(new Wheres(String.valueOf(key), values, Wheres.Operation.IN));
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

    /**
     * 添加条件优先级 SQL表现为 (
     *
     * @return
     */
    public Query addBeginPriority() {
        Wheres wheres = this.wheres.get(this.wheres.size() - 1);
        wheres.addBeginPriority();
        return this;
    }

    /**
     * 添加条件优先级 SQL表现为 )
     *
     * @return
     */
    public Query addEndPriority() {
        Wheres wheres = this.wheres.get(this.wheres.size() - 1);
        wheres.addEndPriority();
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
        return whs;
    }

    public Class getObjectClass() {
        return clazz;
    }

    public Object getIdValue() {
        return idValue;
    }

    public void setIdValue(Object idValue) {
        this.idValue = idValue;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void clearJoin() {
        joins.clear();
    }

    public void clearWheres() {
        wheres.clear();
    }
}
