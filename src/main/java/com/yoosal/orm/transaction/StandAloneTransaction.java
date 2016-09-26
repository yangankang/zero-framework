package com.yoosal.orm.transaction;

import com.yoosal.orm.core.SessionOperation;
import com.yoosal.orm.exception.DatabaseOperationException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class StandAloneTransaction implements Transaction {
    private static final Logger logger = Logger.getLogger(StandAloneTransaction.class);
    private SessionOperation sessionOperation;
    private CRCallback crCallback;
    private Connection connection;
    private boolean canCommit = true;

    public StandAloneTransaction(SessionOperation sessionOperation, Connection connection) {
        this.sessionOperation = sessionOperation;
        this.connection = connection;
    }

    public StandAloneTransaction() {
    }

    public StandAloneTransaction(SessionOperation sessionOperation, Connection connection, boolean canCommit) {
        this.sessionOperation = sessionOperation;
        this.connection = connection;
        this.canCommit = canCommit;
    }

    @Override
    public void setCallback(CRCallback callback) {
        this.crCallback = callback;
    }

    @Override
    public void setIsolation(Isolation isolation) throws SQLException {
        int c = 0;
        if (isolation.equals(Isolation.TRANSACTION_NONE)) {
            c = Connection.TRANSACTION_NONE;
        }
        if (isolation.equals(Isolation.TRANSACTION_READ_COMMITTED)) {
            c = Connection.TRANSACTION_READ_COMMITTED;
        }
        if (isolation.equals(Isolation.TRANSACTION_READ_UNCOMMITTED)) {
            c = Connection.TRANSACTION_READ_UNCOMMITTED;
        }
        if (isolation.equals(Isolation.TRANSACTION_REPEATABLE_READ)) {
            c = Connection.TRANSACTION_REPEATABLE_READ;
        }
        if (isolation.equals(Isolation.TRANSACTION_SERIALIZABLE)) {
            c = Connection.TRANSACTION_SERIALIZABLE;
        }
        connection.setTransactionIsolation(c);
    }

    @Override
    public void begin() throws SQLException {
        if (connection == null) {
            throw new SQLException("开启事务失败没有发现连接对象");
        }
        connection.setAutoCommit(false);
        logger.debug("开启事物");
    }

    @Override
    public void commit() throws SQLException {
        if (canCommit) {
            connection.commit();
            connection.setAutoCommit(true);
            if (crCallback != null) {
                this.crCallback.call();
            }
            if (sessionOperation != null) {
                sessionOperation.close();
                TransactionManger.breakTransaction(sessionOperation);
            }
            logger.debug("提交事物");
        }
    }

    @Override
    public void rollback() {
        try {
            if (connection != null && !connection.getAutoCommit() && canCommit) {
                connection.rollback();
                connection.setAutoCommit(true);
                if (crCallback != null) {
                    this.crCallback.call();
                }
                if (sessionOperation != null) {
                    sessionOperation.close();
                    TransactionManger.breakTransaction(sessionOperation);
                }
                logger.debug("回滚事物");
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            throw new DatabaseOperationException("rollback throw", e);
        }
    }
}
