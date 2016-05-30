package com.yoosal.orm.mapping;

import com.yoosal.orm.core.DataSourceManager;

public interface DBMapping {
    void doMapping(DataSourceManager dataSourceManager, boolean canAlter);
}
