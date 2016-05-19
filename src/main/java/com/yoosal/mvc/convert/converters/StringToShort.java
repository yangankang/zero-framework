package com.yoosal.mvc.convert.converters;

public class StringToShort extends StringToObject {

    public StringToShort() {
        super(Short.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Short.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Short number = (Short) object;
        return number.toString();
    }

}