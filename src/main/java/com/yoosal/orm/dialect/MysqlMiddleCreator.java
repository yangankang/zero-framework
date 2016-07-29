package com.yoosal.orm.dialect;

import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.mapping.MediumText;
import com.yoosal.orm.mapping.Text;
import com.yoosal.orm.query.Limit;

public class MysqlMiddleCreator extends MiddleCreator {

    static {
        typesMapping.put(Text.class, "TEXT");
        typesMapping.put(MediumText.class, "MEDIUMTEXT");
        typesMapping.put(Boolean.class, "BOOLEAN");
        typesMapping.put(boolean.class, "BOOLEAN");
        typesMapping.put(Double.class, "DOUBLE");
        typesMapping.put(double.class, "DOUBLE");
    }

    @Override
    public String getDBType() {
        return DataSourceManager.SupportList.MYSQL.toString();
    }

    @Override
    public void setEngine(SQLChain chain) {
        //"ENGINE = InnoDB DEFAULT CHARSET utf8;"
        chain.engine().setEquals().setValue("InnoDB").defaultCommand().charset().setValue("utf8");
    }

    @Override
    public void setLimit(SQLChain chain, Limit limit) {
        chain.limit().setValue(limit.getStart()).setSplit().setValue(limit.getLimit());
    }
}
