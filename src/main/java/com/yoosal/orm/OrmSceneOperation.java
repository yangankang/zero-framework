package com.yoosal.orm;

import com.yoosal.orm.query.Query;

import java.util.List;

public class OrmSceneOperation implements Operation {
    @Override
    public void begin() {

    }

    @Override
    public Object save(ModelObject object) {
        return null;
    }

    @Override
    public void update(ModelObject object) {

    }

    @Override
    public void updates(List<ModelObject> objects) {

    }

    @Override
    public void remove(Query query) {

    }

    @Override
    public List<ModelObject> list(Query query) {
        return null;
    }

    @Override
    public ModelObject query(Query query) {
        return null;
    }

    @Override
    public long count(Query query) {
        return 0;
    }

    @Override
    public void commit() {

    }
}
