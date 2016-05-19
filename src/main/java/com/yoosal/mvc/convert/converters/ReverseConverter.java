package com.yoosal.mvc.convert.converters;

public class ReverseConverter implements com.yoosal.mvc.convert.converters.Converter {

    private com.yoosal.mvc.convert.converters.TwoWayConverter converter;

    public ReverseConverter(TwoWayConverter converter) {
        this.converter = converter;
    }

    public Class getSourceClass() {
        return converter.getTargetClass();
    }

    public Class getTargetClass() {
        return converter.getSourceClass();
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        return converter.convertTargetToSourceClass(source, targetClass);
    }

}
