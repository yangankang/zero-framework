package com.yoosal.orm.core;

public class MySqlDataSourceResolve implements DataSourceResolve {
    @Override
    public String getDBType() {
        return "MYSQL";
    }

    @Override
    public Class getDataSourceClass() throws ClassNotFoundException {
        return Class.forName("org.logicalcobwebs.proxool.ProxoolDataSource");
    }
}
