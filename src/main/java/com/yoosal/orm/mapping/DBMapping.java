package com.yoosal.orm.mapping;

import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.dialect.SQLDialect;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Set;

public interface DBMapping {
    void doMapping(DataSourceManager dataSourceManager, Set<Class> classes, boolean canAlter) throws SQLException;

    void register(SQLDialect dialect);

    SQLDialect getSQLDialect(DatabaseMetaData databaseMetaData) throws SQLException;
}
