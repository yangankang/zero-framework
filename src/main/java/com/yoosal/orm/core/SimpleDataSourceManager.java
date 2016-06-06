package com.yoosal.orm.core;

import com.yoosal.common.Logger;
import com.yoosal.common.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SimpleDataSourceManager implements DataSourceManager {
    private static final Logger logger = Logger.getLogger(DataSourceManager.class);
    private static final Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
    private static final List<GroupDataSource> groupDataSources = new ArrayList<GroupDataSource>();
    private static final Map<String, DataSourceResolve> dataSourceResolve = new HashMap<String, DataSourceResolve>();

    private static final String DATA_SOURCE_NAME_KEY = "dataSourceName";
    private static final String DATA_SOURCE_GROUP_KEY = "group";
    private static final String DATA_SOURCE_TABLES_KEY = "tables";
    private static final String DATA_SOURCE_CLASS_KEY = "class";

    static {
        dataSourceResolve.put(SupportList.MYSQL.toString(), new MySqlDataSourceResolve());
    }

    @Override
    public synchronized void addDataSource(GroupDataSource groupDataSource) {
        groupDataSources.add(groupDataSource);
        for (GroupDataSource.SourceObject gds : groupDataSource.getSourceObjects()) {
            dataSourceMap.put(gds.getDataSourceName(), gds.getDataSource());
            logger.info("got a dataSource from add:" + gds.getDataSourceName());
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
        return new HashSet<GroupDataSource>(groupDataSources);
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
            if (keySplit.length >= 4) {
                Map<String, String> map = splitProperties.get(keySplit[2]);
                if (map == null) {
                    map = new HashMap<String, String>();
                }
                map.put(keySplit[3], String.valueOf(entry.getValue()));
                splitProperties.put(keySplit[2], map);
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
        Class clazz = null;
        if (clazz == null) {
            String dataSourceClassString = map.get(DATA_SOURCE_CLASS_KEY);
            if (StringUtils.isNotBlank(dataSourceClassString)) {
                clazz = Class.forName(dataSourceClassString);
            }
        }
        if (clazz == null) {
            DataSourceResolve resolve = dataSourceResolve.get(dbType);
            if (resolve != null) {
                clazz = resolve.getDataSourceClass();
            }
        }
        if (clazz != null) {
            Object object = clazz.newInstance();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String method = "set" + (key.substring(0, 1).toUpperCase()) + key.substring(1, key.length());
                Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(method)) {
                        Class<?>[] parameterTypes = m.getParameterTypes();
                        if (parameterTypes != null) {
                            if (parameterTypes[0].isAssignableFrom(int.class)) {
                                m.invoke(object, new Object[]{Integer.parseInt(value)});
                            } else if (parameterTypes[0].isAssignableFrom(long.class)) {
                                m.invoke(object, new Object[]{Long.parseLong(value)});
                            } else if (parameterTypes[0].isAssignableFrom(double.class)) {
                                m.invoke(object, new Object[]{Long.parseLong(value)});
                            } else {
                                m.invoke(object, new Object[]{value});
                            }
                        } else {
                            m.invoke(object);
                        }
                    }
                }
            }
            String dataSourceName = map.get(DATA_SOURCE_NAME_KEY);
            String dataSourceGroup = map.get(DATA_SOURCE_GROUP_KEY);
            String tablesString = map.get(DATA_SOURCE_TABLES_KEY);

            GroupDataSource groupDataSource = new GroupDataSource();
            groupDataSource.setGroupName(dataSourceGroup);
            groupDataSource.addGroup(dataSourceName, (DataSource) object);
            if (StringUtils.isNotBlank(tablesString)) {
                groupDataSource.setEnumNames(Arrays.asList(tablesString.split("\\.")));
            }
            groupDataSources.add(groupDataSource);
            dataSourceMap.put(dataSourceName, (DataSource) object);
            DataSource dataSource = (DataSource) object;
            logger.info("got a dataSource from properties:" + dataSourceName);

            return dataSource;
        }
        return null;
    }
}
