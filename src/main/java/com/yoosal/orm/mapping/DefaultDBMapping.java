package com.yoosal.orm.mapping;

import com.yoosal.common.AnnotationUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.orm.annotation.Column;
import com.yoosal.orm.annotation.Table;
import com.yoosal.orm.core.DataSourceManager;
import com.yoosal.orm.exception.OrmMappingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultDBMapping implements DBMapping {
    private DataSourceManager dataSourceManager;
    private Set<Class> classes;
    private Map<Class, TableModel> mappingModelMap = new HashMap<Class, TableModel>();

    @Override
    public void doMapping(DataSourceManager dataSourceManager, Set<Class> classes, boolean canAlter) {
        this.dataSourceManager = dataSourceManager;
        this.classes = classes;

        classToModel();
    }

    private void classToModel() {
        if (classes != null) {
            for (Class clazz : classes) {
                Table table = AnnotationUtils.findAnnotation(clazz, Table.class);
                if (table == null) continue;
                TableModel model = new TableModel();
                String tableName = table.value();
                model.setJavaTableName(tableName);
                String dataSourceName = table.dataSourceName();
                if (StringUtils.isNotBlank(dataSourceName)) {
                    model.setDataSourceName(dataSourceName);
                }
                model.convert();

                Object[] objects = clazz.getEnumConstants();
                int i = 0;
                for (Object obj : objects) {
                    ColumnModel columnModel = new ColumnModel();
                    columnModel.setCode(i);
                    columnModel.setJavaName(obj.toString());
                    Column column = null;
                    try {
                        column = clazz.getField(obj.toString()).getAnnotation(Column.class);
                    } catch (NoSuchFieldException e) {
                        throw new OrmMappingException("not find Column in field " + tableName + "." + obj.toString(), e);
                    }
                    String name = column == null ? null : column.name();
                    if (StringUtils.isBlank(name)) name = null;
                    Class type = column == null ? String.class : column.type();
                    long length = column == null ? 255 : column.length();
                    if (length == 255 && type == Integer.class) length = 11;
                    if (length == 255 && type == Long.class) length = 13;
                    if (length == 255 && type == Double.class) length = 16;
                    if (length == 255 && type == Text.class) length = 0;
                    int isPrimaryKey = (column == null ? 0 : column.key());
                    Class generateStrategy = (column == null ? null : column.strategy());
                    if (generateStrategy != null && generateStrategy.isAssignableFrom(Column.class))
                        generateStrategy = null;
                    boolean isLock = (column == null ? false : column.lock());
                    columnModel.setIsPrimaryKey(isPrimaryKey);
                    columnModel.setJavaAliasName(name);
                    columnModel.setJavaType(type);
                    columnModel.setLength(length);
                    columnModel.setGenerateStrategy(generateStrategy);
                    columnModel.setLock(isLock);
                    columnModel.convert();
                    model.addMappingColumnModel(columnModel);
                    i++;
                }
                mappingModelMap.put(clazz, model);
            }
        }
    }
}
