package com.yoosal.orm.dialect;

public interface SQLDialect {

    String getType(int columnTypeInt);
}
