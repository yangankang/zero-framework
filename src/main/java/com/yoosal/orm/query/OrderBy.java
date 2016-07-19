package com.yoosal.orm.query;

public class OrderBy {
    public enum Type {
        /**
         * 排序的升序标记
         */
        ASC,
        /**
         * 排序的降序标记
         */
        DESC
    }

    private Type type = Type.ASC;
    private Object field;

    public Type getType() {
        if (type == null) {
            return Type.ASC;
        }
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getField() {
        return field;
    }

    public void setField(Object field) {
        this.field = field;
    }
}
