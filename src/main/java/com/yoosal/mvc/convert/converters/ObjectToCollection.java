package com.yoosal.mvc.convert.converters;

import com.yoosal.common.JdkVersion;
import com.yoosal.common.type.GenericCollectionTypeResolver;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;

import java.lang.reflect.Constructor;
import java.util.*;

public class ObjectToCollection implements com.yoosal.mvc.convert.converters.Converter {

    private ConversionService conversionService;

    private ConversionExecutor elementConverter;

    public ObjectToCollection(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ObjectToCollection(ConversionExecutor elementConverter) {
        this.elementConverter = elementConverter;
    }

    public Class getSourceClass() {
        return Object.class;
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
        ConversionExecutor converter = getElementConverter(source, targetClass);
        Object value;
        if (converter != null) {
            value = converter.execute(source);
        } else {
            value = source;
        }
        collection.add(value);
        return collection;
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

    private ConversionExecutor getElementConverter(Object source, Class targetClass) {
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