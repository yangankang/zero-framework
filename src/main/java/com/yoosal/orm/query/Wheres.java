package com.yoosal.orm.query;

import java.util.ArrayList;
import java.util.List;

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

    public enum Logic {
        AND, OR
    }

    /**
     * 用来设置 OR 和 AND的优先级 SQl表现为括号 ()
     */
    public enum Priority {
        BEGIN, END
    }

    private Logic logic = Logic.AND;
    private String key;
    private Object value;
    private Operation operation = Operation.EQUAL;

    private List<Priority> begins = new ArrayList<Priority>();
    private List<Priority> ends = new ArrayList<Priority>();

    /**
     * in 操作符，判断某列是否包含
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres in(Object key, Object value) {
        this.set(key, value, Operation.IN);
        return this;
    }

    /**
     * 相等操作，判断和value相等的值
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres equal(Object key, Object value) {
        this.set(key, value, Operation.EQUAL);
        return this;
    }

    /**
     * 不等于操作
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres notEqual(Object key, Object value) {
        this.set(key, value, Operation.NOT_EQUAL);
        return this;
    }

    /**
     * 大于比较符，某列的值是否大约value
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres gt(Object key, Object value) {
        this.set(key, value, Operation.GT);
        return this;
    }

    /**
     * 大于等于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres gte(Object key, Object value) {
        this.set(key, value, Operation.GT_EQUAL);
        return this;
    }

    /**
     * 小于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres lt(Object key, Object value) {
        this.set(key, value, Operation.LT);
        return this;
    }

    /**
     * 小于等于比较符
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres lte(Object key, Object value) {
        this.set(key, value, Operation.LT_EQUAL);
        return this;
    }

    /**
     * like 操作
     *
     * @param key
     * @param value
     * @return
     */
    public Wheres like(Object key, Object value) {
        this.set(key, value, Operation.LIKE);
        return this;
    }


    public static Wheres or() {
        Wheres wheres = new Wheres();
        wheres.setLogic(Logic.OR);
        return wheres;
    }

    public static Wheres and() {
        Wheres wheres = new Wheres();
        wheres.setLogic(Logic.AND);
        return wheres;
    }

    public Wheres() {

    }

    public Wheres(Object key, Object value, Operation operation) {
        this.key = String.valueOf(key);
        this.value = value;
        this.operation = operation;
    }

    public Wheres(Object key, Object value, Operation operation, Logic logic) {
        this.key = String.valueOf(key);
        this.value = value;
        this.operation = operation;
        this.logic = logic;
    }

    public Wheres(Object key, Object value, Logic logic) {
        this.key = String.valueOf(key);
        this.value = value;
        this.logic = logic;
    }

    public Wheres(String key, Object value) {
        this.key = key;
        this.value = value;
        this.operation = Operation.EQUAL;
    }

    public void set(Object key, Object value) {
        this.key = String.valueOf(key);
        this.value = value;
        this.operation = Operation.EQUAL;
    }

    public void set(Object key, Object value, Operation operation) {
        this.key = String.valueOf(key);
        this.value = value;
        this.operation = operation;
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
        return getOperation(operation);
    }

    public static String getOperation(Wheres.Operation operation) {
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

    public Logic getLogic() {
        if (logic == null) {
            return Logic.AND;
        }
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    protected void addBeginPriority() {
        begins.add(Priority.BEGIN);
    }

    protected void addEndPriority() {
        begins.add(Priority.END);
    }

    public List<Priority> getBegins() {
        return begins;
    }

    public List<Priority> getEnds() {
        return ends;
    }
}
