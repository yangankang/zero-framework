package com.yoosal.mvc.convert.converters;

import com.yoosal.common.JdkVersion;
import com.yoosal.common.type.GenericCollectionTypeResolver;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionService;

import java.util.*;

public class CollectionToCollection implements com.yoosal.mvc.convert.converters.Converter {

    private ConversionService conversionService;

    private ConversionExecutor elementConverter;

    public CollectionToCollection(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public CollectionToCollection(ConversionExecutor elementConverter) {
        this.elementConverter = elementConverter;
    }

    public Class getSourceClass() {
        return Collection.class;
    }

    public Class getTargetClass() {
        return Collection.class;
    }

    public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
        if (source == null) {
            return null;
        }
        Class targetCollectionImpl = getCollectionImplClass(targetClass);
        Collection targetCollection = (Collection) targetCollectionImpl.getConstructor(null).newInstance(null);
        ConversionExecutor elementConverter = getElementConverter(source, targetClass);
        Collection sourceCollection = (Collection) source;
        Iterator it = sourceCollection.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (elementConverter != null) {
                value = elementConverter.execute(value);
            }
            targetCollection.add(value);
        }
        return targetCollection;
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