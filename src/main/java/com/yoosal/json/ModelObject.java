package com.yoosal.json;

public class ModelObject extends JSONObject {
    private Class clazz;

    public static ModelObject create(Class clazz) {
        return new ModelObject(clazz);
    }

    public ModelObject(Class clazz) {
        this.clazz = clazz;
    }

    public ModelObject() {
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public ModelObject clone() {
        return new ModelObject(clazz);
    }
}
