package com.yoosal.orm;

import com.yoosal.orm.query.Query;

import java.util.List;

/**
 * 数据库基本操作的基类，直接面向开发者的接口
 */
public interface Operation {
    Object save(ModelObject object);

    void update(ModelObject object);

    void updates(List<ModelObject> objects);

    void remove(Query query);

    List<ModelObject> list(Query query);

    ModelObject query(Query query);

    long count(Query query);
}
