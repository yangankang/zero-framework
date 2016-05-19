package com.yoosal.mvc.convert.converters;

import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;

import java.lang.reflect.Array;

public class ArrayToArray implements com.yoosal.mvc.convert.converters.Converter {

    private ConversionService conversionService;

    private ConversionExecutor elementConverter;

    public ArrayToArray(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ArrayToArray(ConversionExecutor elementConverter) {
        this.elementConverter = elementConverter;
    }

    public Class getSourceClass() {
        return Object[].class;
    }

    public Class getTargetClass() {
        return Object[].class;
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        if (source == null) {
            return null;
        }
        Class sourceComponentType = source.getClass().getComponentType();
        Class targetComponentType = targetClass.getComponentType();
        int length = Array.getLength(source);
        Object targetArray = Array.newInstance(targetComponentType, length);
        ConversionExecutor converter = getElementConverter(sourceComponentType, targetComponentType);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(source, i);
            Array.set(targetArray, i, converter.execute(value));
        }
        return targetArray;
    }

    private ConversionExecutor getElementConverter(Class sourceComponentType, Class targetComponentType) {
        if (elementConverter != null) {
            return elementConverter;
        } else {
            return conversionService.getConversionExecutor(sourceComponentType, targetComponentType);
        }
    }
}
