package com.yoosal.orm.core;

/**
 * 用于检查字段的有效性，比如电话号码
 */
public interface Check {

    boolean check(Object value);

    int verify(Object value);
}
