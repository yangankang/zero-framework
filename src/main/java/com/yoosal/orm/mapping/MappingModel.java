package com.yoosal.orm.mapping;

import java.util.List;

public class MappingModel extends AbstractModelCheck {
    private String javaTableName;
    private String dbTableName;
    private String dataSourceName;
    private List<MappingColumnModel> mappingColumnModels;

    public String getJavaTableName() {
        return javaTableName;
    }

    public void setJavaTableName(String javaTableName) {
        this.javaTableName = javaTableName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public List<MappingColumnModel> getMappingColumnModels() {
        return mappingColumnModels;
    }

    public void setMappingColumnModels(List<MappingColumnModel> mappingColumnModels) {
        this.mappingColumnModels = mappingColumnModels;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public String getName() {
        return javaTableName;
    }

    @Override
    public void setMappingName(String name) {
        this.dbTableName = name;
    }
}
