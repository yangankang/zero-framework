package com.yoosal.orm.dialect;

import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Join;
import com.yoosal.orm.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreatorJoinModel {
    private TableModel tableModel;
    private String tableAsName;
    private Map<String, String> columnAsName;
    private Query query;
    private Join join;
    private List<CreatorJoinModel> child;
    private Map<String, String> javaColumnAsName;

    public boolean hasChild() {
        if (child == null || child.size() <= 0) {
            return false;
        }
        return true;
    }

    public boolean isQuery() {
        return query != null ? true : false;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public String getTableAsName() {
        return tableAsName;
    }

    public void setTableAsName(String tableAsName) {
        this.tableAsName = tableAsName;
    }

    public Map<String, String> getColumnAsName() {
        return columnAsName;
    }

    public void setColumnAsName(Map<String, String> columnAsName) {
        this.columnAsName = columnAsName;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }

    public List<CreatorJoinModel> getChild() {
        return child;
    }

    public void setChild(List<CreatorJoinModel> child) {
        this.child = child;
    }

    public void addChild(CreatorJoinModel child) {
        if (!child.isQuery()) {
            this.child.add(child);
        }
    }

    public CreatorJoinModel getModelByTableModel(TableModel tableModel) {
        if (this.getTableModel() == tableModel) {
            return this;
        }
        for (CreatorJoinModel model : child) {
            if (model.getTableModel() == tableModel) {
                return model;
            }
        }
        return null;
    }

    public Map<String, String> getJavaColumnAsName() {
        return javaColumnAsName;
    }

    public void setJavaColumnAsName(Map<String, String> javaChildColumnAsName) {
        this.javaColumnAsName = javaChildColumnAsName;
    }

    public List<CreatorJoinModel> getSelectColumns() {
        List<CreatorJoinModel> joinModels = new ArrayList<CreatorJoinModel>();
        joinModels.add(this);
        if (null != child)
            joinModels.addAll(child);
        return joinModels;
    }
}
