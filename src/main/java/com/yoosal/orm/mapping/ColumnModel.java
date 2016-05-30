package com.yoosal.orm.mapping;

public class ColumnModel extends AbstractModelCheck {
    private String javaName;
    private String javaAliasName;
    private Class javaType;
    private String javaAliasType;
    private int code;

    private String columnName;
    private String columnType;
    private long length;
    private Class generateStrategy;

    private int isPrimaryKey;
    private boolean isLock = false;

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getJavaAliasName() {
        return javaAliasName;
    }

    public void setJavaAliasName(String javaAliasName) {
        this.javaAliasName = javaAliasName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
    }

    public String getJavaAliasType() {
        return javaAliasType;
    }

    public void setJavaAliasType(String javaAliasType) {
        this.javaAliasType = javaAliasType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Class getGenerateStrategy() {
        return generateStrategy;
    }

    public void setGenerateStrategy(Class generateStrategy) {
        this.generateStrategy = generateStrategy;
    }

    public int getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(int isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    @Override
    protected String getName() {
        return this.javaName;
    }

    @Override
    protected void setMappingName(String name) {
        this.columnName = name;
    }
}
