package com.yoosal.mvc.convert.converters;

public class StringToInteger extends com.yoosal.mvc.convert.converters.StringToObject {

    public StringToInteger() {
        super(Integer.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Integer.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Integer number = (Integer) object;
        return number.toString();
    }
}