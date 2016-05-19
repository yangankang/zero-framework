package com.yoosal.mvc.convert.converters;

public class StringToEnum extends StringToObject {

    public StringToEnum() {
        super(Enum.class);
    }

    protected Object toObject(String string, Class targetClass) throws Exception {
        return Enum.valueOf(targetClass, string);
    }

    protected String toString(Object object) throws Exception {
        return object.toString();
    }

}