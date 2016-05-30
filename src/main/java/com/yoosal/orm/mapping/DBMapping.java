package com.yoosal.orm.mapping;

import com.yoosal.orm.core.DataSourceManager;

import java.sql.SQLException;
import java.util.Set;

public interface DBMapping {
    void doMapping(DataSourceManager dataSourceManager, Set<Class> classes, boolean canAlter) throws SQLException;
}
