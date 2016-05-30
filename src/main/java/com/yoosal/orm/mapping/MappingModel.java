package com.yoosal.orm.mapping;

import java.util.List;

public class MappingModel {
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

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public List<MappingColumnModel> getMappingColumnModels() {
        return mappingColumnModels;
    }

    public void setMappingColumnModels(List<MappingColumnModel> mappingColumnModels) {
        this.mappingColumnModels = mappingColumnModels;
    }
}
