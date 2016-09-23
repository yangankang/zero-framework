package com.yoosal.orm.mapping;

import com.yoosal.orm.annotation.AutoIncrementStrategy;

import java.util.*;

public class TableModel extends AbstractModelCheck {
    private String javaTableName;
    private String dbTableName;
    private String dataSourceName;
    private List<ColumnModel> mappingColumnModels;
    private List<ColumnModel> mappingPrimaryKeyColumnModels;
    private Boolean hasAutoIncrementPrimaryKey = null;
    private Map<String, ColumnModel> columnModelMap = null;

    public ColumnModel getColumnByJavaName(Object key) {
        if (columnModelMap == null) {
            columnModelMap = new HashMap<String, ColumnModel>();
            for (ColumnModel cm : mappingColumnModels) {
                columnModelMap.put(cm.getJavaName(), cm);
            }
        }
        return columnModelMap.get(String.valueOf(key));
    }

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
        if (this.mappingColumnModels.size() > 0) {
            columnModel.setPreviousColumnModel(this.mappingColumnModels.get(this.mappingColumnModels.size() - 1));
        }
        this.mappingColumnModels.add(columnModel);
    }

    public List<ColumnModel> getMappingPrimaryKeyColumnModels() {
        if (mappingPrimaryKeyColumnModels == null) {
            synchronized (this) {
                if (mappingPrimaryKeyColumnModels == null) {
                    for (ColumnModel cm : mappingColumnModels) {
                        if (mappingPrimaryKeyColumnModels == null) {
                            mappingPrimaryKeyColumnModels = new ArrayList<ColumnModel>();
                        }
                        if (cm.isPrimaryKey()) {
                            mappingPrimaryKeyColumnModels.add(cm);
                        }
                    }
                }
            }
        }
        return mappingPrimaryKeyColumnModels;
    }

    public boolean hasAutoIncrementPrimaryKey() {
        List<ColumnModel> columnModels = getMappingPrimaryKeyColumnModels();
        if (hasAutoIncrementPrimaryKey != null) {
            return hasAutoIncrementPrimaryKey;
        }
        for (ColumnModel cm : columnModels) {
            if (cm.getGenerateStrategy() != null && cm.getGenerateStrategy().equals(AutoIncrementStrategy.class)) {
                hasAutoIncrementPrimaryKey = new Boolean(true);
                return hasAutoIncrementPrimaryKey;
            }
        }
        return false;
    }

    public boolean haPrimaryKey() {
        List<ColumnModel> columnModels = getMappingPrimaryKeyColumnModels();
        if (columnModels != null && columnModels.size() > 0) {
            return true;
        }
        return false;
    }
}
