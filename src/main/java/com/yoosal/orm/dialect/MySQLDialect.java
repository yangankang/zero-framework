package com.yoosal.orm.dialect;

import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.mapping.Text;

public class MySQLDialect extends StandardSQL {

    static {
        typesMapping.put(Text.class, "TEXT");
        typesMapping.put(Long.class, "FLOAT");
        typesMapping.put(Boolean.class, "BOOLEAN");
        typesMapping.put(Double.class, "DOUBLE");
    }

    @Override
    public String createTable(TableModel tableModel) {
        String ctstring = super.createTable(tableModel);
        ctstring += "ENGINE = InnoDB DEFAULT CHARSET utf8;";
        return ctstring;
    }

    @Override
    public String getDBType() {
        return DataSourceManager.SupportList.MYSQL.toString();
    }
}
