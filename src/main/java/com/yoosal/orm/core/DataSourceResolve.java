package com.yoosal.orm.core;

public interface DataSourceResolve {
    String getDBType();

    Class getDataSourceClass();
}
