package com.yoosal.mvc.convert.converters;

import com.yoosal.common.ClassUtils;

public class StringToClass extends StringToObject {

    private ClassLoader classLoader;

    public StringToClass(ClassLoader classLoader) {
        super(Class.class);
        this.classLoader = classLoader;
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return ClassUtils.forName(string, classLoader);
    }

    public String toString(Object object) throws Exception {
        Class clazz = (Class) object;
        return clazz.getName();
    }
}