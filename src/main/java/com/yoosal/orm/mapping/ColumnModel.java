package com.yoosal.orm.mapping;

public class ColumnModel extends AbstractModelCheck {
    private String javaName;
    private Class javaType;
    private int code;   //排序的数值

    private String columnName;
    private String columnType;
    private int columnTypeCode;
    private long length;
    private Class generateStrategy;

    private int isPrimaryKey;
    private boolean isLock = false;
    private boolean isIndex = false;
    private String indexName;

    public boolean isIndex() {
        return isIndex;
    }

    public void setIndex(boolean index) {
        isIndex = index;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public Class getJavaType() {
        return javaType;
    }

    public void setJavaType(Class javaType) {
        this.javaType = javaType;
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

    public int getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(int columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
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
