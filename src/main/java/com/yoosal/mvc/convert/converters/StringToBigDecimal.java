package com.yoosal.mvc.convert.converters;

import java.math.BigDecimal;

public class StringToBigDecimal extends com.yoosal.mvc.convert.converters.StringToObject {

    public StringToBigDecimal() {
        super(BigDecimal.class);
    }

    public Object toObject(String string, Class objectClass) throws Exception {
        return new BigDecimal(string);
    }

    public String toString(Object object) throws Exception {
        BigDecimal number = (BigDecimal) object;
        return number.toString();
    }

}