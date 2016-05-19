package com.yoosal.mvc.convert.converters;

import com.yoosal.common.JdkVersion;
import com.yoosal.common.type.GenericCollectionTypeResolver;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

public class ArrayToCollection implements com.yoosal.mvc.convert.converters.TwoWayConverter {

    private ConversionService conversionService;

    private ConversionExecutor elementConverter;

    public ArrayToCollection(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ArrayToCollection(ConversionExecutor elementConverter) {
        this.elementConverter = elementConverter;
    }

    public Class getSourceClass() {
        return Object[].class;
    }

    public Class getTargetClass() {
        return Collection.class;
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        if (source == null) {
            return null;
        }
        Class collectionImplClass = getCollectionImplClass(targetClass);
        Constructor constructor = collectionImplClass.getConstructor(null);
        Collection collection = (Collection) constructor.newInstance(null);
        ConversionExecutor converter = getArrayElementConverter(source, targetClass);
        int length = Array.getLength(source);
        for (int i = 0; i < length; i++) {
            Object value = Array.get(source, i);
            if (converter != null) {
                value = converter.execute(value);
            }
            collection.add(value);
        }
        return collection;
    }

    public Object convertTargetToSourceClass(Object target, Class sourceClass) throws Exception {
        if (target == null) {
            return null;
        }
        Collection collection = (Collection) target;
        Object array = Array.newInstance(sourceClass.getComponentType(), collection.size());
        int i = 0;
        for (Iterator it = collection.iterator(); it.hasNext(); i++) {
            Object value = it.next();
            if (value != null) {
                ConversionExecutor converter;
                if (elementConverter != null) {
                    converter = elementConverter;
                } else {
                    converter = conversionService.getConversionExecutor(value.getClass(), sourceClass
                            .getComponentType());
                }
                value = converter.execute(value);
            }
            Array.set(array, i, value);
        }
        return array;
    }

    private Class getCollectionImplClass(Class targetClass) {
        if (targetClass.isInterface()) {
            if (List.class.equals(targetClass)) {
                return ArrayList.class;
            } else if (Set.class.equals(targetClass)) {
                return LinkedHashSet.class;
            } else if (SortedSet.class.equals(targetClass)) {
                return TreeSet.class;
            } else {
                throw new IllegalArgumentException("Unsupported collection interface [" + targetClass.getName() + "]");
            }
        } else {
            return targetClass;
        }
    }

    private ConversionExecutor getArrayElementConverter(Object source, Class targetClass) {
        if (elementConverter != null) {
            return elementConverter;
        } else {
            if (JdkVersion.isAtLeastJava15()) {
                Class elementType = GenericCollectionTypeResolver.getCollectionType(targetClass);
                if (elementType != null) {
                    Class componentType = source.getClass().getComponentType();
                    return conversionService.getConversionExecutor(componentType, elementType);
                }
            }
            return null;
        }
    }

}