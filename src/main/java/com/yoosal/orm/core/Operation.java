package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.query.Query;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库基本操作的基类，直接面向开发者的接口
 */
public interface Operation {

    enum Isolation {
        TRANSACTION_NONE,
        TRANSACTION_READ_UNCOMMITTED,
        TRANSACTION_READ_COMMITTED,
        TRANSACTION_REPEATABLE_READ,
        TRANSACTION_SERIALIZABLE
    }

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

    ModelObject save(ModelObject object);

    void update(ModelObject object);

    void updates(Batch batch);

    void remove(Query query);

    List<ModelObject> list(Query query);

    ModelObject query(Query query);

    long count(Query query);

    /**
     * 提交事务
     */
    void commit() throws SQLException;

    void rollback();
}
