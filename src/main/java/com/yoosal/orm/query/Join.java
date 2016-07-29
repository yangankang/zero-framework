package com.yoosal.orm.query;

import com.yoosal.common.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 只有 left join ,join时分为左表和右表，左表一般是主查询
 * 右表一般是Join查询
 */
public class Join {

    private Class leftClass;
    private Class rightClass;
    private String dataSourceName;
    /**
     * 这里的wheres的用法和Query中的用法有差异，这个只使用条件判断值比如
     * 等于 大于 小于 等等，每一个Wheres的key代表左表的字段，value代表的
     * 是右表的字段，比如：
     * left join t_b b on a.bid = b.bid left join t_c c on a.cid = c.cid
     */
    private List<Wheres> wheres = new ArrayList<Wheres>();

    /**
     * 查询完成后，当前查询的数据会成为ModelObject的一个K-V，这里的K就是对应
     * 的joinName，如果没有joinName那么会默认用Enum的类名
     */
    private String joinName;

    /**
     * 查询的值是否是数组
     */
    private boolean isMulti = true;

    public static Join join(Class rightClass) {
        return new Join(rightClass);
    }

    public static Join join(Class leftClass, Class rightClass) {
        return new Join(leftClass, rightClass);
    }

    public static Join where(Class rightClass, Object key, Object value) {
        Join join = new Join(rightClass);
        join.where(key, value);
        return join;
    }

    public static Join where(Class leftClass, Class rightClass, Object key, Object value) {
        Join join = new Join(leftClass, rightClass);
        join.where(key, value);
        return join;
    }

    public Join(Class rightClass) {
        this.rightClass = rightClass;
    }

    public Join(Class leftClass, Class rightClass) {
        this.leftClass = leftClass;
        this.rightClass = rightClass;
    }

    public Join(Class<Enum> rightClass, String dataSourceName) {
        this.rightClass = rightClass;
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

    public Join one(Object key, Object value) {
        this.wheres.add(new Wheres(String.valueOf(key), value));
        this.isMulti = false;
        return this;
    }

    public String getJoinName() {
        if (StringUtils.isBlank(joinName)) {
            joinName = rightClass.getSimpleName();
        }
        return joinName;
    }

    public Join setJoinName(String joinName) {
        this.joinName = joinName;
        return this;
    }

    public Join setObjectClass(Class rightClass) {
        this.rightClass = rightClass;
        return this;
    }

    public Class<Enum> getObjectClass() {
        return rightClass;
    }

    public Class getSourceObjectClass() {
        return leftClass;
    }

    public void setSourceObjectClass(Class leftClass) {
        this.leftClass = leftClass;
    }

    public Join setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        return this;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public List<Wheres> getWheres() {
        return wheres;
    }

    public Join setWheres(List<Wheres> wheres) {
        this.wheres = wheres;
        return this;
    }

    public Join one() {
        this.isMulti = false;
        return this;
    }

    public Join multi() {
        this.isMulti = true;
        return this;
    }

    public boolean isMulti() {
        return this.isMulti;
    }
}
