package com.yoosal.orm;

import com.yoosal.common.CollectionUtils;
import com.yoosal.common.Logger;
import com.yoosal.common.scan.DefaultFrameworkScanClass;
import com.yoosal.common.scan.FrameworkScanClass;
import com.yoosal.orm.annotation.Table;
import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.core.SimpleDataSourceManager;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.DefaultDBMapping;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * ORM 框架的所有配置Bean，和mvc的很相似
 */
public class OperationManager {
    private static final Logger logger = Logger.getLogger(OperationManager.class);
    /**
     * 全局配置缓存的Map
     */
    private static final Map<String, Object> properties = new HashMap<String, Object>();

    static final String KEY_SCAN_PACKAGE = "orm.scan.package";
    static final String KEY_MAPPING_CLASS = "orm.mapping.";
    //映射的时候如果没有发现字段，是否允许添加字段,Boolean类型
    static final String KEY_MAPPING_ALTER = "orm.mapping.alter";
    /**
     * 配置数据库连接，所有的数据都是对应的连接池中的DataSource的属性，比如：
     * orm.ds.proxool.dbType=mysql
     * orm.ds.proxool.driver=com.mysql.jdbc.Driver
     */
    static final String KEY_DATASOURCE_INFO = "orm.ds.";

    private static FrameworkScanClass frameworkScanClass = new DefaultFrameworkScanClass();
    private static DataSourceManager dataSourceManager = new SimpleDataSourceManager();
    private static DBMapping mapping = new DefaultDBMapping();

    /**
     * 存放所有的映射为表的Class 类
     */
    private static Set<Class> classesFromScan = new HashSet<Class>();

    public void setProperties(Properties prop) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (prop != null) {
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                properties.put((String) entry.getKey(), entry.getValue());
            }
        }
        Set<String> classNames = getConfigMappingClasses();
        this.propertiesToClass(classNames);
        this.scanClassInSet(getScanPackage());
        initDataSource();

        doMapping();
    }

    private void initDataSource() throws InvocationTargetException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Map<String, Object> dsmap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getKey().startsWith(KEY_DATASOURCE_INFO)) {
                dsmap.put(entry.getKey(), entry.getValue());
            }
        }
        dataSourceManager.fromProperties(dsmap);
    }

    private void propertiesToClass(Set<String> classNames) throws ClassNotFoundException {
        for (String className : classNames) {
            classesFromScan.add(Class.forName(className));
        }
    }

    private static Object getProperty(String key) {
        if (CollectionUtils.isEmpty(properties)) {
            logger.error("not found orm config");
        }
        return properties.get(key);
    }

    public void setMappingClassByString(Set<String> classNames) throws ClassNotFoundException {
        this.propertiesToClass(classNames);
    }

    public static DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    public void scanClassInSet(String packageName) {
        Set<Class> classes = frameworkScanClass.getScanClass(packageName, Table.class);
        if (classes != null) {
            for (Class clazz : classes) {
                classesFromScan.add(clazz);
            }
        }
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public static String getScanPackage() {
        return (String) getProperty(KEY_SCAN_PACKAGE);
    }

    public static Set<String> getConfigMappingClasses() {
        Set<String> strings = new HashSet<String>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getKey().startsWith(KEY_MAPPING_CLASS)) {
                strings.add(String.valueOf(entry.getValue()));
            }
        }
        return strings;
    }

    public static Set<Class> getClasses() {
        return classesFromScan;
    }

    void doMapping() {
        boolean isCanAlter = canAlter();
        mapping.doMapping(dataSourceManager, classesFromScan, isCanAlter);
    }

    public static DBMapping getMapping() {
        return mapping;
    }

    public static boolean canAlter() {
        String canAlter = (String) getProperty(KEY_MAPPING_ALTER);
        if (canAlter.equalsIgnoreCase("false")) {
            return false;
        } else {
            return true;
        }
    }
}
