package com.yoosal.mvc.convert.converters;

import java.math.BigInteger;

public class StringToBigInteger extends com.yoosal.mvc.convert.converters.StringToObject {

    public StringToBigInteger() {
        super(BigInteger.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return new BigInteger(string);
    }

    public String toString(Object object) throws Exception {
        BigInteger number = (BigInteger) object;
        return number.toString();
    }
}