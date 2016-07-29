package com.yoosal.orm.core;

import com.yoosal.orm.mapping.DBMapping;

public interface SessionOperation extends Operation {
    void close();

    void setDataSourceManager(DataSourceManager dataSourceManager);

    void setDbMapping(DBMapping dbMapping);
}
