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
    public enum Operation {
        EQUAL, IN, LIKE, NOT_EQUAL, GT, GT_EQUAL, LT, LT_EQUAL
    }

    public enum Order {
        /**
         * 排序的升序标记
         */
        asc,
        /**
         * 排序的降序标记
         */
        desc
    }

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
        this.operation = Operation.EQUAL;
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


    public String getOperation() {
        /**
         * static final String operationEqual = "=";
         * static final String operationIn = "in";
         * static final String operationLike = "like";
         * static final String operationNotEqual = "!=";
         * static final String operationGt = ">";
         * static final String operationGtEqual = ">=";
         * static final String operationLt = "<";
         * static final String operationLtEqual = "<=";
         */
        if (operation.equals(Operation.EQUAL)) {
            return "=";
        } else if (operation.equals(Operation.IN)) {
            return "in";
        } else if (operation.equals(Operation.LIKE)) {
            return "like";
        } else if (operation.equals(Operation.NOT_EQUAL)) {
            return "!=";
        } else if (operation.equals(Operation.GT)) {
            return ">";
        } else if (operation.equals(Operation.GT_EQUAL)) {
            return ">=";
        } else if (operation.equals(Operation.LT)) {
            return "<";
        } else if (operation.equals(Operation.LT_EQUAL)) {
            return "<=";
        }

        return "=";
    }

    public Operation getEnumOperation() {
        return this.operation;
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

    public boolean isNormal() {
        if (this.type == 0) {
            return true;
        }
        return false;
    }
}
