package com.yoosal.orm.mapping;

import com.yoosal.orm.core.DataSourceManager;

import java.util.Set;

public class DefaultDBMapping implements DBMapping {
    private DataSourceManager dataSourceManager;
    private Set<Class> classes;

    @Override
    public void doMapping(DataSourceManager dataSourceManager, Set<Class> classes, boolean canAlter) {
        this.dataSourceManager = dataSourceManager;
        this.classes = classes;


    }
}
