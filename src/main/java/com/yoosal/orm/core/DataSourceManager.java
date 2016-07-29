package com.yoosal.orm.core;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public interface DataSourceManager {
    /**
     * 默认支持的数据库，每个数据库默认一个连接池框架
     */
    enum SupportList {
        MYSQL, ORACLE, SQL_SERVER
    }

    void addDataSource(GroupDataSource groupDataSource);

    void removeDataSource(String dataSourceName);

    Set<GroupDataSource> getAllDataSource();

    DataSource getDataSource(String dataSourceName);

    DataSource getMasterDataSource();

    DataSource getSlaveDataSource();

    void setMasterDataSource(DataSource dataSource);

    void setSlaveDataSource(DataSource dataSource);

    void fromProperties(Map<String, Object> properties) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException;

    void registerDataSourceResolve(DataSourceResolve resolve);
}
