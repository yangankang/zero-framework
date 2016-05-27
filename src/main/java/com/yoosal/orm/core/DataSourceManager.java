package com.yoosal.orm.core;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

public interface DataSourceManager {

    void addDataSource(GroupDataSource groupDataSource);

    void removeDataSource(String dataSourceName);

    Set<GroupDataSource> getAllDataSource();

    DataSource getDataSource(String dataSourceName);

    DataSource getDataSource();

    void fromProperties(Map<String, Object> properties) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException;

    void registerDataSourceResolve(DataSourceResolve resolve);
}
