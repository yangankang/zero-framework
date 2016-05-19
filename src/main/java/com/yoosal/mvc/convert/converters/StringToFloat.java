package com.yoosal.mvc.convert.converters;

public class StringToFloat extends StringToObject {

    public StringToFloat() {
        super(Float.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Float.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Float number = (Float) object;
        return number.toString();
    }

}