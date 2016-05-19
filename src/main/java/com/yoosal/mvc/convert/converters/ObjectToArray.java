package com.yoosal.mvc.convert.converters;

import com.yoosal.json.JSON;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;

import java.lang.reflect.Array;

public class ObjectToArray implements com.yoosal.mvc.convert.converters.Converter {

    private ConversionService conversionService;

    private ConversionExecutor elementConverter;

    public ObjectToArray(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ObjectToArray(ConversionExecutor elementConverter) {
        this.elementConverter = elementConverter;
    }

    public Class getSourceClass() {
        return Object.class;
    }

    public Class getTargetClass() {
        return Object[].class;
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        if (source == null) {
            return null;
        }
        Class componentType = targetClass.getComponentType();
        Object array = Array.newInstance(componentType, 1);
        ConversionExecutor converter;
        if (elementConverter != null) {
            converter = elementConverter;
        } else {
            converter = conversionService.getConversionExecutor(source.getClass(), componentType);
        }
        Array.set(array, 0, converter.execute(source));
        return array;
    }
}