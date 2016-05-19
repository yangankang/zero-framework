package com.yoosal.mvc.convert.converters;

public class StringToLong extends StringToObject {

    public StringToLong() {
        super(Long.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Long.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Long number = (Long) object;
        return number.toString();
    }

}