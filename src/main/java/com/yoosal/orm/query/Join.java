package com.yoosal.orm.query;

import java.util.ArrayList;
import java.util.List;

/**
 * 只有 left join 和 inner join，join时分为左表和右表，左表一般是主查询
 * 右表一般是Join查询
 */
public class Join {
    private Class<Enum> clazz;
    private String dataSourceName;
    /**
     * 这里的wheres的用法和Query中的用法有差异，这个只使用条件判断值比如
     * 等于 大于 小于 等等，每一个Wheres的key代表左表的字段，value代表的
     * 是右表的字段，如果在同一个数据库中则可以表现为：
     * left join t_b b on a.bid = b.bid left join t_c c on a.cid = c.cid
     */
    private List<Wheres> wheres = new ArrayList<Wheres>();

    /**
     * 查询完成后，当前查询的数据会成为ModelObject的一个K-V，这里的K就是对应
     * 的joinName，如果没有joinName那么会默认用Enum的类名
     */
    private String joinName;

    public static Join join(Class clazz) {
        return new Join(clazz);
    }

    public static Join where(Class clazz, Object key, Object value) {
        Join join = new Join(clazz);
        join.where(key, value);
        return join;
    }

    public static Join where(Class clazz, Object key, Object value, Wheres.Operation operation) {
        Join join = new Join(clazz);
        join.where(key, value, operation);
        return join;
    }

    public Join(Class<Enum> clazz) {
        this.clazz = clazz;
    }

    public Join(Class<Enum> clazz, String dataSourceName) {
        this.clazz = clazz;
        this.dataSourceName = dataSourceName;
    }

    public Join where(Wheres wheres) {
        this.wheres.add(wheres);
        return this;
    }

    public Join where(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value));
        return this;
    }

    public Join where(Object key, Object value, Wheres.Operation operation) {
        this.wheres.add(new Wheres(String.valueOf(key), value, operation));
        return this;
    }

    public String getJoinName() {
        return joinName;
    }

    public void setJoinName(String joinName) {
        this.joinName = joinName;
    }

    public Class<Enum> getClazz() {
        return clazz;
    }

    public void setClazz(Class<Enum> clazz) {
        this.clazz = clazz;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public List<Wheres> getWheres() {
        return wheres;
    }

    public void setWheres(List<Wheres> wheres) {
        this.wheres = wheres;
    }
}
