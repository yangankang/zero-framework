package com.yoosal.orm.mapping;

import com.yoosal.orm.annotation.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableModel extends AbstractModelCheck {
    private String javaTableName;
    private String dbTableName;
    private String dataSourceName;
    private List<ColumnModel> mappingColumnModels;
    private List<ColumnModel> mappingPrimaryKeyColumnModels;
    private Boolean hasAutoIncrementPrimaryKey = null;

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

    public List<ColumnModel> getMappingPrimaryKeyColumnModels() {
        if (mappingPrimaryKeyColumnModels == null) {
            synchronized (this) {
                if (mappingPrimaryKeyColumnModels == null) {
                    for (ColumnModel cm : mappingColumnModels) {
                        if (mappingPrimaryKeyColumnModels == null) {
                            mappingPrimaryKeyColumnModels = new ArrayList<ColumnModel>();
                        }
                        if (cm.getIsPrimaryKey() > 0) {
                            mappingPrimaryKeyColumnModels.add(cm);
                        }
                    }
                    if (mappingPrimaryKeyColumnModels != null) {
                        Collections.sort(mappingColumnModels, new Comparator<ColumnModel>() {
                            @Override
                            public int compare(ColumnModel o1, ColumnModel o2) {
                                return o2.getIsPrimaryKey() - o1.getIsPrimaryKey();
                            }
                        });
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
            if (cm.getGenerateStrategy().equals(Column.class)) {
                hasAutoIncrementPrimaryKey = new Boolean(true);
                return hasAutoIncrementPrimaryKey;
            }
        }
        return false;
    }
}
