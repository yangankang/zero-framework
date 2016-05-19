package com.yoosal.mvc.convert.converters;

public class StringToDouble extends StringToObject {

    public StringToDouble() {
        super(Double.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Double.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Double number = (Double) object;
        return number.toString();
    }

}