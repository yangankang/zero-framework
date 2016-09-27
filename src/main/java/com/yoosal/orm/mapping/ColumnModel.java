package com.yoosal.orm.mapping;

import com.yoosal.orm.annotation.AutoIncrementStrategy;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.DefaultValue;
import com.yoosal.orm.core.IDStrategy;
import com.yoosal.orm.exception.OrmMappingException;

public class ColumnModel extends AbstractModelCheck {
    private String javaName;
    private Class javaType;
    private int code;   //排序的数值

    private String columnName;
    private String columnType;
    private int columnTypeCode;
    private long length;
    private Class<IDStrategy> generateStrategy;
    private IDStrategy generateStrategyInstance;

    private boolean isPrimaryKey;
    private boolean isLock = false;
    private boolean isIndex = false;
    private boolean isAllowNull = true;
    private DefaultValue defaultValue;
    private String indexName;
    private String comment;

    private ColumnModel previousColumnModel;

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

    public void setGenerateStrategy(Class<IDStrategy> generateStrategy) {
        this.generateStrategy = generateStrategy;
        try {
            if (generateStrategy != null && !generateStrategy.isAnnotation()) {
                generateStrategyInstance = generateStrategy.newInstance();
            }
        } catch (InstantiationException e) {
            throw new OrmMappingException("instance generateStrategy class throw", e);
        } catch (IllegalAccessException e) {
            throw new OrmMappingException("instance generateStrategy class throw", e);
        }
    }

    public IDStrategy getIDStrategy() {
        return generateStrategyInstance;
    }

    public int getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(int columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isKey() {
        return false;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public boolean isAllowNull() {
        return isAllowNull;
    }

    public void setAllowNull(boolean allowNull) {
        isAllowNull = allowNull;
    }

    public DefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(DefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    protected String getName() {
        return this.javaName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    protected void setMappingName(String name) {
        this.columnName = name;
    }

    public boolean isAutoIncrement() {
        if (this.isPrimaryKey() && generateStrategy != null && generateStrategy.equals(AutoIncrementStrategy.class)) {
            return true;
        }
        return false;
    }

    public ColumnModel getPreviousColumnModel() {
        return previousColumnModel;
    }

    public void setPreviousColumnModel(ColumnModel previousColumnModel) {
        this.previousColumnModel = previousColumnModel;
    }
}
