package com.yoosal.mvc.convert.converters;

public class StringToByte extends StringToObject {

    public StringToByte() {
        super(Byte.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return Byte.valueOf(string);
    }

    public String toString(Object object) throws Exception {
        Byte number = (Byte) object;
        return number.toString();
    }

}