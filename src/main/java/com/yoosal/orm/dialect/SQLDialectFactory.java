package com.yoosal.orm.dialect;

import com.yoosal.orm.OperationManager;
import com.yoosal.orm.core.DataSourceManager;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SQLDialectFactory {
    private static final Map<String, SQLDialect> registerSQLDialect = new ConcurrentHashMap<String, SQLDialect>();

    static {
        registerSQLDialect.put(DataSourceManager.SupportList.MYSQL.toString(), new MySQLDialect());
    }

    public static SQLDialect getSQLDialect(DatabaseMetaData databaseMetaData) throws SQLException {
        String dataBaseName = databaseMetaData.getDatabaseProductName();
        boolean isShowSQL = OperationManager.isShowSQL();
        for (Map.Entry<String, SQLDialect> entry : registerSQLDialect.entrySet()) {
            String key = entry.getKey();
            if (dataBaseName.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
                SQLDialect sqlDialect = entry.getValue();
                sqlDialect.setShowSQL(isShowSQL);
                return sqlDialect;
            }
        }
        return null;
    }

    public static void registerSQLDialect(SQLDialect dialect) {
        registerSQLDialect.put(dialect.getDBType(), dialect);
    }
}
