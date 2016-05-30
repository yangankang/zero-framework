package com.yoosal.orm.core;

import javax.sql.DataSource;
import java.util.*;

/**
 * 把数据源分组，暂时没用到
 */
public class GroupDataSource {
    private String groupName;
    /**
     * 能映射到数据库的枚举，表示当前的连接只会和这些表关联，比如创建表
     */
    private List<String> enumNames;
    private Set<SourceObject> sourceObjects;

    public List<String> getEnumNames() {
        return enumNames;
    }

    public void setEnumNames(List<String> enumNames) {
        this.enumNames = enumNames;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Set<SourceObject> getSourceObjects() {
        return sourceObjects;
    }

    public void setSourceObjects(Set<SourceObject> sourceObjects) {
        this.sourceObjects = sourceObjects;
    }

    public SourceObject addGroup(String dataSourceName, DataSource dataSource) {
        SourceObject sourceObject = new SourceObject();
        sourceObject.setDataSource(dataSource);
        sourceObject.setDataSourceName(dataSourceName);
        if (sourceObjects == null) {
            sourceObjects = new HashSet<SourceObject>();
        }
        sourceObjects.add(sourceObject);
        return sourceObject;
    }

    class SourceObject {
        private String dataSourceName;
        private DataSource dataSource;

        public String getDataSourceName() {
            return dataSourceName;
        }

        public void setDataSourceName(String dataSourceName) {
            this.dataSourceName = dataSourceName;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }
    }
}
