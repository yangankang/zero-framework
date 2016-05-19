package com.yoosal.mvc.convert.converters;

public interface Converter {

    Class getSourceClass();

    Class getTargetClass();

    Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception;

}