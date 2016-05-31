package com.yoosal.orm.dialect;

import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.mapping.Text;

import java.util.List;

public class MySQLDialect extends StandardSQL {

    static {
        typesMapping.put(Text.class, "TEXT");
        typesMapping.put(Integer.class, "INT");
        typesMapping.put(Long.class, "FLOAT");
        typesMapping.put(Boolean.class, "BOOLEAN");
        typesMapping.put(Double.class, "DOUBLE");
    }

    @Override
    public String addColumn(TableModel tableModel, List<ColumnModel> existColumns) {
        return null;
    }

    @Override
    public String createTable(TableModel tableModel) {
        return null;
    }

    @Override
    public String getDBType() {
        return DataSourceManager.SupportList.MYSQL.toString();
    }
}
