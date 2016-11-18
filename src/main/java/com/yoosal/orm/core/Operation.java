package com.yoosal.orm.core;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.query.Query;
import com.yoosal.orm.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库基本操作的基类，直接面向开发者的接口
 */
public interface Operation {

    Transaction beginTransaction() throws SQLException;

    Transaction createTransaction();

    boolean isTransacting();

    ModelObject save(ModelObject object);

    void update(ModelObject object);

    void update(ModelObject editor, ModelObject criteria);

    void updates(Batch batch);

    void remove(Query query);

    List<ModelObject> list(Query query);

    ModelObject query(Query query);

    long count(Query query);
}
