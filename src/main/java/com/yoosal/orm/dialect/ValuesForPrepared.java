package com.yoosal.orm.dialect;

import com.yoosal.orm.mapping.ColumnModel;

public class ValuesForPrepared {
    private String sql;
    private ColumnModel[] keys;
    private int count;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ColumnModel[] getKeys() {
        return keys;
    }

    public void setKeys(ColumnModel[] keys) {
        this.keys = keys;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
