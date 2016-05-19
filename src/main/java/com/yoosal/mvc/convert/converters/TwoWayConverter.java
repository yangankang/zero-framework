package com.yoosal.mvc.convert.converters;

public interface TwoWayConverter extends com.yoosal.mvc.convert.converters.Converter {

    Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception;

}