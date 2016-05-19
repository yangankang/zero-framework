package com.yoosal.mvc.convert.converters;

import com.yoosal.common.NumberUtils;

public class NumberToNumber implements com.yoosal.mvc.convert.converters.Converter {

    public Class getSourceClass() {
        return Number.class;
    }

    public Class getTargetClass() {
        return Number.class;
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        return NumberUtils.convertNumberToTargetClass((Number) source, targetClass);
    }

}