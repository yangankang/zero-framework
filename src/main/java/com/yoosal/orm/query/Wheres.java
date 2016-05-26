package com.yoosal.orm.query;

/**
 * 单条查询语句中包含的所有条件,条件列表：
 * static final String operationEqual = "=";
 * static final String operationIn = "in";
 * static final String operationLike = "like";
 * static final String operationNotEqual = "!=";
 * static final String operationGt = ">";
 * static final String operationGtEqual = ">=";
 * static final String operationLt = "<";
 * static final String operationLtEqual = "<=";
 */
public class Wheres {
    enum Operation {
        EQUAL, IN, LIKE, NOT_EQUAL, GT, GT_EQUAL, LT, LT_EQUAL
    }

    /**
     * 排序的升序标记
     */
    public static final byte ORDER_ASC = 41;
    /**
     * 排序的降序标记
     */
    public static final byte ORDER_DESC = 42;

    public static final byte TYPE_ID = 1;
    public static final byte TYPE_START = 2;
    public static final byte TYPE_LIMIT = 3;
    public static final byte TYPE_ORDER = 4;

    private String key;
    private Object value;
    private Operation operation;

    /**
     * 操作类型标记，比如ID操作，排序操作，分页操作
     */
    private int type = 0;

    /**
     * in 操作符，判断某列是否包含
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres in(Object key, Object value) {
        return new Wheres(key, value, Operation.IN);
    }

    /**
     * 相等操作，判断和value相等的值
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres equal(Object key, Object value) {
        return new Wheres(key, value, Operation.EQUAL);
    }

    /**
     * 不等于操作
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres notEqual(Object key, Object value) {
        return new Wheres(key, value, Operation.NOT_EQUAL);
    }

    /**
     * 大于比较符，某列的值是否大约value
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres gt(Object key, Object value) {
        return new Wheres(key, value, Operation.GT);
    }

    /**
     * 大于等于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres gte(Object key, Object value) {
        return new Wheres(key, value, Operation.GT_EQUAL);
    }

    /**
     * 小于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres lt(Object key, Object value) {
        return new Wheres(key, value, Operation.LT);
    }

    /**
     * 小于等于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres lte(Object key, Object value) {
        return new Wheres(key, value, Operation.LT_EQUAL);
    }

    /**
     * like 操作
     *
     * @param key
     * @param value
     * @return
     */
    public static Wheres like(Object key, Object value) {
        return new Wheres(key, value, Operation.LIKE);
    }

    public Wheres(Object key, Object value, Operation operation) {
        this.key = String.valueOf(key);
        this.value = value;
        this.operation = operation;
    }

    public Wheres(String key, Object value) {
        this.key = key;
        this.value = value;
        this.operation = Operation.EQUAL;
    }

    public Wheres(String key, Object value, int type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
