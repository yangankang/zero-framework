package com.yoosal.orm;

import com.yoosal.orm.core.GroupDataSource;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

public class SpringOperationManager extends OperationManager implements InitializingBean {
    private String scanPackage;
    private Set<String> mapping;
    private boolean isAlter;
    private DataSource dataSource;
    private Map<String, DataSource> dataSourceMap;
    private String convert;
    private boolean isShowSql;

    public void setConvert(String convert) {
        this.convert = convert;
        this.setProperty(KEY_MAPPING_CONVERT, convert);
    }

    public void setShowSql(boolean showSql) {
        isShowSql = showSql;
        this.setProperty(KEY_MAPPING_SHOW_SQL, showSql);
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        this.scanClassInSet(scanPackage);
    }

    public void setMapping(Set<String> mapping) throws ClassNotFoundException {
        this.mapping = mapping;
        this.setMappingClassByString(mapping);
    }

    public void setAlter(boolean alter) {
        isAlter = alter;
        this.setProperty(KEY_MAPPING_ALTER, String.valueOf(alter));
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        GroupDataSource groupDataSource = new GroupDataSource();
        groupDataSource.addGroup(null, dataSource);
        getDataSourceManager().addDataSource(groupDataSource);
        getDataSourceManager().setMasterDataSource(dataSource);
    }

    public void setDataSourceMap(Map<String, DataSource> dataSourceMap) {
        this.dataSourceMap = dataSourceMap;
        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
            GroupDataSource groupDataSource = new GroupDataSource();
            groupDataSource.addGroup(entry.getKey(), dataSource);
            getDataSourceManager().addDataSource(groupDataSource);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        doMapping();
    }

}
