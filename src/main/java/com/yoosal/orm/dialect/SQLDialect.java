package com.yoosal.orm.dialect;

import com.yoosal.orm.ModelObject;
import com.yoosal.orm.core.Batch;
import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.mapping.ColumnModel;
import com.yoosal.orm.mapping.DBMapping;
import com.yoosal.orm.mapping.TableModel;
import com.yoosal.orm.query.Query;

import java.util.List;

public interface SQLDialect {

    String getType(int columnTypeInt);

    String getType(Class clazz);

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

    DataSourceManager.SupportList getEnumType();

    /**
     * 为PreparedStatement提供数据
     *
     * @param tableMapping
     * @param object
     * @return
     */
    ValuesForPrepared prepareInsert(TableModel tableMapping, ModelObject object);

    ValuesForPrepared prepareUpdate(TableModel tableMapping, ModelObject object);

    /**
     * 这个只是单纯的修改的值和条件值
     *
     * @param tableModel
     * @param editor
     * @param criteria
     * @return
     */
    ValuesForPrepared prepareUpdate(TableModel tableModel, ModelObject editor, ModelObject criteria);

    ValuesForPrepared prepareUpdateBatch(TableModel tableMapping, Batch batch);

    ValuesForPrepared prepareDelete(DBMapping tableMapping, Query query);

    ValuesForPrepared prepareSelect(DBMapping dbMapping, Query query);

    ValuesForPrepared prepareSelectCount(TableModel tableMapping, Query query);

    void setShowSQL(boolean isShowSQL);

}
