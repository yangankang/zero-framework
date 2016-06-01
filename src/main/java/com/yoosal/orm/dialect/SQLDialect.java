package com.yoosal.orm.dialect;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.TableModel;

import java.util.List;

public interface SQLDialect {

    String getType(int columnTypeInt);

    /**
     * @param tableModel   表映射信息
     * @param existColumns 新增的字段
     * @return
     */
    String addColumn(TableModel tableModel, List<ColumnModel> existColumns);

    String createTable(TableModel tableModel);

    /**
     * 获得数据库字段
     *
     * @return
     */
    String getDBType();

    String insert(TableModel tableMapping, ModelObject object);
}
