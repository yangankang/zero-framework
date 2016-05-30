package com.yoosal.orm.mapping;

import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractModelCheck {
    private String javaTableName;
    private String dbTableName;
    private String dataSourceName;
    private List<ColumnModel> mappingColumnModels;

    public String getJavaTableName() {
        return javaTableName;
    }

    public void setJavaTableName(String javaTableName) {
        this.javaTableName = javaTableName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public List<ColumnModel> getMappingColumnModels() {
        return mappingColumnModels;
    }

    public void setMappingColumnModels(List<ColumnModel> mappingColumnModels) {
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

    public void addMappingColumnModel(ColumnModel columnModel) {
        if (this.mappingColumnModels == null) {
            this.mappingColumnModels = new ArrayList<ColumnModel>();
        }
        this.mappingColumnModels.add(columnModel);
    }
}
