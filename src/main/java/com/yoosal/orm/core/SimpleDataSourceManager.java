package com.yoosal.orm.core;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleDataSourceManager implements DataSourceManager {
    private static final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<String, DataSource>();
    private static final List<GroupDataSource> groupDataSources = new CopyOnWriteArrayList<GroupDataSource>();
    private static final Map<String, DataSourceResolve> dataSourceResolve = new HashMap<String, DataSourceResolve>();

    private static final String DATA_SOURCE_NAME_KEY = "dataSourceName";
    private static final String DATA_SOURCE_GROUP_KEY = "group";

    /**
     * 默认支持的数据库，每个数据库默认一个连接池框架
     */
    enum SupportList {
        MYSQL
    }

    static {
        dataSourceResolve.put(SupportList.MYSQL.toString(), new MySqlDataSourceResolve());
    }

    @Override
    public synchronized void addDataSource(GroupDataSource groupDataSource) {
        groupDataSources.add(groupDataSource);
        for (GroupDataSource.SourceObject gds : groupDataSource.getSourceObjects()) {
            dataSourceMap.put(gds.getDataSourceName(), gds.getDataSource());
        }
    }

    @Override
    public synchronized void removeDataSource(String dataSourceName) {
        groupDataSources.remove(dataSourceName);
        for (GroupDataSource gds : groupDataSources) {
            for (GroupDataSource.SourceObject so : gds.getSourceObjects()) {
                if (so.getDataSourceName().equalsIgnoreCase(dataSourceName)) {
                    gds.getSourceObjects().remove(so);
                }
            }
        }
    }

    @Override
    public Set<GroupDataSource> getAllDataSource() {
        Set set = new HashSet();
        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    @Override
    public DataSource getDataSource(String dataSourceName) {
        return dataSourceMap.get(dataSourceName);
    }

    @Override
    public DataSource getDataSource() {
        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            return entry.getValue();
        }
        return null;
    }

    @Override
    public void fromProperties(Map<String, Object> properties) throws IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        Map<String, Map<String, String>> splitProperties = new HashMap();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String key = entry.getKey();
            String[] keySplit = key.split("\\.");
            if (keySplit.length > 4) {
                Map<String, String> map = splitProperties.get(keySplit[3]);
                if (map == null) {
                    map = new HashMap<String, String>();
                }
                map.put(keySplit[3], String.valueOf(entry.getValue()));
                splitProperties.put(keySplit[3], map);
            }
        }

        for (Map.Entry<String, Map<String, String>> entry : splitProperties.entrySet()) {
            propertiesToDatSource(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void registerDataSourceResolve(DataSourceResolve resolve) {
        dataSourceResolve.put(resolve.getDBType(), resolve);
    }

    private DataSource propertiesToDatSource(String dbType, Map<String, String> map) throws IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        DataSourceResolve resolve = dataSourceResolve.get(dbType);
        if (resolve != null) {
            Class clazz = resolve.getDataSourceClass();
            Object object = clazz.newInstance();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String method = "set" + (key.substring(0, 1).toUpperCase()) + key.substring(1, key.length());
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(method)) {
                        m.invoke(object, value);
                    }
                }
            }
            String dataSourceName = map.get(DATA_SOURCE_NAME_KEY);
            String dataSourceGroup = map.get(DATA_SOURCE_GROUP_KEY);
            GroupDataSource groupDataSource = new GroupDataSource();
            groupDataSource.setGroupName(dataSourceGroup);
            groupDataSource.addGroup(dataSourceName, (DataSource) object);
            groupDataSources.add(groupDataSource);
            dataSourceMap.put(dataSourceName, (DataSource) object);
            return (DataSource) object;
        }
        return null;
    }
}
