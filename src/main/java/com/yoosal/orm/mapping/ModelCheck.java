package com.yoosal.orm.mapping;

/**
 * 用于检查对比Java的字段和数据库的字段是否一致，如果有字段转换则使用字段转换
 */
public interface ModelCheck {
    String convert();

    boolean compare(String columnOrTableName);

    boolean compareAndSet(String columnOrTableName);

    void setWordConvert(WordConvert convert);

    String getName();

    void setMappingName(String name);
}
