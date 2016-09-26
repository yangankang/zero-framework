package com.yoosal.orm.transaction;

import java.sql.SQLException;

public interface Transaction {

    enum Isolation {
        TRANSACTION_NONE,
        TRANSACTION_READ_UNCOMMITTED,
        TRANSACTION_READ_COMMITTED,
        TRANSACTION_REPEATABLE_READ,
        TRANSACTION_SERIALIZABLE
    }

    interface CRCallback {
        void call();
    }

    void setCallback(CRCallback callback);

    /**
     * 使用
     * Connection.TRANSACTION_NONE
     * Connection.TRANSACTION_READ_UNCOMMITTED
     * Connection.TRANSACTION_READ_COMMITTED
     * Connection.TRANSACTION_REPEATABLE_READ
     * Connection.TRANSACTION_SERIALIZABLE
     * 作为参数
     */
    void setIsolation(Isolation isolation) throws SQLException;

    /**
     * Transaction 的开始，
     */
    void begin() throws SQLException;

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    void rollback();
}
