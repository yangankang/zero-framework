package com.yoosal.orm.mapping;

import com.yoosal.orm.core.DataSourceManager;

public class DefaultDBMapping implements DBMapping {
    private static DataSourceManager dataSourceManager;

    @Override
    public void doMapping(DataSourceManager dataSourceManager, boolean canAlter) {
        this.dataSourceManager = dataSourceManager;
    }
}
