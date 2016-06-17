/*
 * Copyright 2004-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yoosal.mvc.convert.service;

import com.yoosal.common.Assert;
import com.yoosal.mvc.convert.ConversionException;
import com.yoosal.mvc.convert.ConversionExecutor;
import com.yoosal.mvc.convert.ConversionExecutorNotFoundException;
import com.yoosal.mvc.convert.ConversionService;
import com.yoosal.mvc.convert.converters.*;

import java.lang.reflect.Modifier;
import java.util.*;

public class GenericConversionService implements ConversionService {

    private final Map sourceClassConverters = new HashMap();

    private final Map customConverters = new HashMap();

    private final Map aliasMap = new HashMap();

    private ConversionService parent;

    public ConversionService getParent() {
        return parent;
    }

    public void setParent(ConversionService parent) {
        this.parent = parent;
    }

    public void addConverter(Converter converter) {
        Class sourceClass = converter.getSourceClass();
        Class targetClass = converter.getTargetClass();
        Map sourceMap = getSourceMap(sourceClass);
        sourceMap.put(targetClass, converter);
        if (converter instanceof TwoWayConverter) {
            sourceMap = getSourceMap(targetClass);
            sourceMap.put(sourceClass, new ReverseConverter((TwoWayConverter) converter));
        }
    }

    public void addConverter(String id, Converter converter) {
        customConverters.put(id, converter);
    }

    public void addAlias(String alias, Class targetType) {
        aliasMap.put(alias, targetType);
    }

    private Map getSourceMap(Class sourceClass) {
        Map sourceMap = (Map) sourceClassConverters.get(sourceClass);
        if (sourceMap == null) {
            sourceMap = new HashMap();
            sourceClassConverters.put(sourceClass, sourceMap);
        }
        return sourceMap;
    }

    public ConversionExecutor getConversionExecutor(Class sourceClass, Class targetClass)
            throws ConversionExecutorNotFoundException {
        Assert.notNull(sourceClass, "The source class to convert from is required");
        Assert.notNull(targetClass, "The target class to convert to is required");
        sourceClass = convertToWrapperClassIfNecessary(sourceClass);
        targetClass = convertToWrapperClassIfNecessary(targetClass);
        if (targetClass.isAssignableFrom(sourceClass)) {
            return new StaticConversionExecutor(sourceClass, targetClass, new NoOpConverter(sourceClass, targetClass));
        }
        // special handling for arrays since they are not indexable classes
        if (sourceClass.isArray()) {
            if (targetClass.isArray()) {
                return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(this));
            } else if (Collection.class.isAssignableFrom(targetClass)) {
                if (!targetClass.isInterface() && Modifier.isAbstract(targetClass.getModifiers())) {
                    throw new IllegalArgumentException("Conversion target class [" + targetClass.getName()
                            + "] is invalid; cannot convert to abstract collection types--"
                            + "request an interface or concrete implementation instead");
                }
                return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(this));
            }
        }
        if (targetClass.isArray()) {
            if (Collection.class.isAssignableFrom(sourceClass)) {
                Converter collectionToArray = new ReverseConverter(new ArrayToCollection(this));
                return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
            } else {
                return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(this));
            }
        }
        Converter converter = findRegisteredConverter(sourceClass, targetClass);
        if (converter != null) {
            // we found a converter
            return new StaticConversionExecutor(sourceClass, targetClass, converter);
        } else {
            if (parent != null) {
                // try the parent
                return parent.getConversionExecutor(sourceClass, targetClass);
            } else {
                throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                        "No ConversionExecutor found for converting from sourceClass [" + sourceClass.getName()
                                + "] to target class [" + targetClass.getName() + "]");
            }
        }
    }

    public ConversionExecutor getConversionExecutor(String id, Class sourceClass, Class targetClass)
            throws ConversionExecutorNotFoundException {
        Assert.hasText(id, "The id of the custom converter is required");
        Assert.notNull(sourceClass, "The source class to convert from is required");
        Assert.notNull(targetClass, "The target class to convert to is required");
        Converter converter = (Converter) customConverters.get(id);
        if (converter == null) {
            if (parent != null) {
                return parent.getConversionExecutor(id, sourceClass, targetClass);
            } else {
                throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                        "No custom ConversionExecutor found with id '" + id + "' for converting from sourceClass ["
                                + sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
            }
        }
        sourceClass = convertToWrapperClassIfNecessary(sourceClass);
        targetClass = convertToWrapperClassIfNecessary(targetClass);
        if (sourceClass.isArray()) {
            Class sourceComponentType = sourceClass.getComponentType();
            if (targetClass.isArray()) {
                Class targetComponentType = targetClass.getComponentType();
                if (converter.getSourceClass().isAssignableFrom(sourceComponentType)) {
                    if (!converter.getTargetClass().isAssignableFrom(targetComponentType)) {
                        throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                                "Custom ConversionExecutor with id '" + id
                                        + "' cannot convert from an array storing elements of type ["
                                        + sourceComponentType.getName() + "] to an array of storing elements of type ["
                                        + targetComponentType.getName() + "]");
                    }
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
                            targetComponentType, converter);
                    return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(elementConverter));
                } else if (converter.getTargetClass().isAssignableFrom(sourceComponentType)
                        && converter instanceof TwoWayConverter) {
                    TwoWayConverter twoWay = (TwoWayConverter) converter;
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType,
                            targetComponentType, new ReverseConverter(twoWay));
                    return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToArray(elementConverter));
                } else {
                    throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                            "Custom ConversionExecutor with id '" + id
                                    + "' cannot convert from an array storing elements of type ["
                                    + sourceComponentType.getName() + "] to an array storing elements of type ["
                                    + targetComponentType.getName() + "]");
                }
            } else if (Collection.class.isAssignableFrom(targetClass)) {
                if (!targetClass.isInterface() && Modifier.isAbstract(targetClass.getModifiers())) {
                    throw new IllegalArgumentException("Conversion target class [" + targetClass.getName()
                            + "] is invalid; cannot convert to abstract collection types--"
                            + "request an interface or concrete implementation instead");
                }
                if (converter.getSourceClass().isAssignableFrom(sourceComponentType)) {
                    // type erasure has prevented us from getting the concrete type, this is best we can do for now
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType, converter
                            .getTargetClass(), converter);
                    return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(
                            elementConverter));
                } else if (converter.getTargetClass().isAssignableFrom(sourceComponentType)
                        && converter instanceof TwoWayConverter) {
                    TwoWayConverter twoWay = (TwoWayConverter) converter;
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceComponentType, converter
                            .getSourceClass(), new ReverseConverter(twoWay));
                    return new StaticConversionExecutor(sourceClass, targetClass, new ArrayToCollection(
                            elementConverter));
                } else {
                    throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                            "Custom ConversionExecutor with id '" + id
                                    + "' cannot convert from array an storing elements type ["
                                    + sourceComponentType.getName() + "] to a collection of type ["
                                    + targetClass.getName() + "]");
                }
            }
        }
        if (targetClass.isArray()) {
            Class targetComponentType = targetClass.getComponentType();
            if (Collection.class.isAssignableFrom(sourceClass)) {
                // type erasure limits us here as well
                if (converter.getTargetClass().isAssignableFrom(targetComponentType)) {
                    ConversionExecutor elementConverter = new StaticConversionExecutor(converter.getSourceClass(),
                            targetComponentType, converter);
                    Converter collectionToArray = new ReverseConverter(new ArrayToCollection(elementConverter));
                    return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
                } else if (converter.getSourceClass().isAssignableFrom(targetComponentType)
                        && converter instanceof TwoWayConverter) {
                    TwoWayConverter twoWay = (TwoWayConverter) converter;
                    ConversionExecutor elementConverter = new StaticConversionExecutor(converter.getTargetClass(),
                            targetComponentType, new ReverseConverter(twoWay));
                    Converter collectionToArray = new ReverseConverter(new ArrayToCollection(elementConverter));
                    return new StaticConversionExecutor(sourceClass, targetClass, collectionToArray);
                } else {
                    throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                            "Custom ConversionExecutor with id '" + id + "' cannot convert from collection of type ["
                                    + sourceClass.getName() + "] to an array storing elements of type ["
                                    + targetComponentType.getName() + "]");
                }
            } else {
                if (converter.getSourceClass().isAssignableFrom(sourceClass)) {
                    if (!converter.getTargetClass().isAssignableFrom(targetComponentType)) {
                        throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                                "Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
                                        + sourceClass.getName() + "] to array holding elements of type ["
                                        + targetComponentType.getName() + "]");
                    }
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceClass,
                            targetComponentType, converter);
                    return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(elementConverter));
                } else if (converter.getTargetClass().isAssignableFrom(sourceClass)
                        && converter instanceof TwoWayConverter) {
                    if (!converter.getSourceClass().isAssignableFrom(targetComponentType)) {
                        throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                                "Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
                                        + sourceClass.getName() + "] to array holding elements of type ["
                                        + targetComponentType.getName() + "]");
                    }
                    TwoWayConverter twoWay = (TwoWayConverter) converter;
                    ConversionExecutor elementConverter = new StaticConversionExecutor(sourceClass,
                            targetComponentType, new ReverseConverter(twoWay));
                    return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToArray(elementConverter));
                }
            }
        }
        if (Collection.class.isAssignableFrom(targetClass)) {
            if (Collection.class.isAssignableFrom(sourceClass)) {
                ConversionExecutor elementConverter;
                // type erasure forces us to do runtime checks of list elements
                if (converter instanceof TwoWayConverter) {
                    elementConverter = new TwoWayCapableConversionExecutor(converter.getSourceClass(), converter
                            .getTargetClass(), (TwoWayConverter) converter);
                } else {
                    elementConverter = new StaticConversionExecutor(converter.getSourceClass(), converter
                            .getTargetClass(), converter);
                }
                return new StaticConversionExecutor(sourceClass, targetClass, new CollectionToCollection(
                        elementConverter));
            } else {
                ConversionExecutor elementConverter;
                // type erasure forces us to do runtime checks of list elements
                if (converter instanceof TwoWayConverter) {
                    elementConverter = new TwoWayCapableConversionExecutor(sourceClass, converter.getTargetClass(),
                            (TwoWayConverter) converter);
                } else {
                    elementConverter = new StaticConversionExecutor(sourceClass, converter.getTargetClass(), converter);
                }
                return new StaticConversionExecutor(sourceClass, targetClass, new ObjectToCollection(elementConverter));
            }
        }
        if (converter.getSourceClass().isAssignableFrom(sourceClass)) {
            if (!converter.getTargetClass().isAssignableFrom(targetClass)) {
                throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                        "Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
                                + sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
            }
            return new StaticConversionExecutor(sourceClass, targetClass, converter);
        } else if (converter.getTargetClass().isAssignableFrom(sourceClass) && converter instanceof TwoWayConverter) {
            if (!converter.getSourceClass().isAssignableFrom(targetClass)) {
                throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                        "Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
                                + sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
            }
            TwoWayConverter twoWay = (TwoWayConverter) converter;
            return new StaticConversionExecutor(sourceClass, targetClass, new ReverseConverter(twoWay));
        } else {
            throw new ConversionExecutorNotFoundException(sourceClass, targetClass,
                    "Custom ConversionExecutor with id '" + id + "' cannot convert from sourceClass ["
                            + sourceClass.getName() + "] to targetClass [" + targetClass.getName() + "]");
        }
    }

    private Converter findRegisteredConverter(Class sourceClass, Class targetClass) {
        if (sourceClass.isInterface()) {
            LinkedList classQueue = new LinkedList();
            classQueue.addFirst(sourceClass);
            while (!classQueue.isEmpty()) {
                Class currentClass = (Class) classQueue.removeLast();
                Map sourceTargetConverters = findConvertersForSource(currentClass);
                Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
                if (converter != null) {
                    return converter;
                }
                Class[] interfaces = currentClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    classQueue.addFirst(interfaces[i]);
                }
            }
            Map objectConverters = findConvertersForSource(Object.class);
            return findTargetConverter(objectConverters, targetClass);
        } else {
            LinkedList classQueue = new LinkedList();
            classQueue.addFirst(sourceClass);
            while (!classQueue.isEmpty()) {
                Class currentClass = (Class) classQueue.removeLast();
                Map sourceTargetConverters = findConvertersForSource(currentClass);
                Converter converter = findTargetConverter(sourceTargetConverters, targetClass);
                if (converter != null) {
                    return converter;
                }
                if (currentClass.getSuperclass() != null) {
                    classQueue.addFirst(currentClass.getSuperclass());
                }
                Class[] interfaces = currentClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    classQueue.addFirst(interfaces[i]);
                }
            }
            return null;
        }
    }

    public Object executeConversion(Object source, Class targetClass) throws ConversionException {
        if (source != null) {
            ConversionExecutor conversionExecutor = getConversionExecutor(source.getClass(), targetClass);
            return conversionExecutor.execute(source);
        } else {
            return null;
        }
    }

    public Object executeConversion(String converterId, Object source, Class targetClass) throws ConversionException {
        if (source != null) {
            ConversionExecutor conversionExecutor = getConversionExecutor(converterId, source.getClass(), targetClass);
            return conversionExecutor.execute(source);
        } else {
            return null;
        }
    }

    public Class getClassForAlias(String name) throws IllegalArgumentException {
        Class clazz = (Class) aliasMap.get(name);
        if (clazz != null) {
            return clazz;
        } else {
            if (parent != null) {
                return parent.getClassForAlias(name);
            } else {
                return null;
            }
        }
    }

    public Set getConversionExecutors(Class sourceClass) {
        Set parentExecutors;
        if (parent != null) {
            parentExecutors = parent.getConversionExecutors(sourceClass);
        } else {
            parentExecutors = Collections.EMPTY_SET;
        }
        Map sourceMap = getSourceMap(sourceClass);
        if (parentExecutors.isEmpty() && sourceMap.isEmpty()) {
            return Collections.EMPTY_SET;
        }
        Set entries = sourceMap.entrySet();
        Set conversionExecutors = new HashSet(entries.size() + parentExecutors.size());
        for (Iterator it = entries.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            Class targetClass = (Class) entry.getKey();
            Converter converter = (Converter) entry.getValue();
            conversionExecutors.add(new StaticConversionExecutor(sourceClass, targetClass, converter));
        }
        conversionExecutors.addAll(parentExecutors);
        return conversionExecutors;
    }

    protected Map getSourceClassConverters() {
        return sourceClassConverters;
    }


    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == null) {
            return true;
        }
        Converter converter = getConverter(sourceType, targetType);
        return (converter != null);
    }

    public <T> T convert(Object source, Class<T> targetType) throws Exception {
        Converter converter = getConverter(source.getClass(), targetType);
        Object object = converter.convertSourceToTargetClass(source, converter.getTargetClass());
        return (T) object;
    }

    protected Converter getConverter(Class sourceClass, Class targetClass) {
        Map sourceTargetConverters = findConvertersForSource(sourceClass);
        return findTargetConverter(sourceTargetConverters, targetClass);
    }

    private Map findConvertersForSource(Class sourceClass) {
        Map sourceConverters = (Map) sourceClassConverters.get(sourceClass);
        return sourceConverters != null ? sourceConverters : Collections.EMPTY_MAP;
    }

    private Converter findTargetConverter(Map sourceTargetConverters, Class targetClass) {
        if (sourceTargetConverters.isEmpty()) {
            return null;
        }
        if (targetClass.isInterface()) {
            LinkedList classQueue = new LinkedList();
            classQueue.addFirst(targetClass);
            while (!classQueue.isEmpty()) {
                Class currentClass = (Class) classQueue.removeLast();
                Converter converter = (Converter) sourceTargetConverters.get(currentClass);
                if (converter != null) {
                    return converter;
                }
                Class[] interfaces = currentClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    classQueue.addFirst(interfaces[i]);
                }
            }
            return (Converter) sourceTargetConverters.get(Object.class);
        } else {
            LinkedList classQueue = new LinkedList();
            classQueue.addFirst(targetClass);
            while (!classQueue.isEmpty()) {
                Class currentClass = (Class) classQueue.removeLast();
                Converter converter = (Converter) sourceTargetConverters.get(currentClass);
                if (converter != null) {
                    return converter;
                }
                if (currentClass.getSuperclass() != null) {
                    classQueue.addFirst(currentClass.getSuperclass());
                }
                Class[] interfaces = currentClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    classQueue.addFirst(interfaces[i]);
                }
            }
            return null;
        }
    }

    private Class convertToWrapperClassIfNecessary(Class targetType) {
        if (targetType.isPrimitive()) {
            if (targetType.equals(int.class)) {
                return Integer.class;
            } else if (targetType.equals(short.class)) {
                return Short.class;
            } else if (targetType.equals(long.class)) {
                return Long.class;
            } else if (targetType.equals(float.class)) {
                return Float.class;
            } else if (targetType.equals(double.class)) {
                return Double.class;
            } else if (targetType.equals(byte.class)) {
                return Byte.class;
            } else if (targetType.equals(boolean.class)) {
                return Boolean.class;
            } else if (targetType.equals(char.class)) {
                return Character.class;
            } else {
                throw new IllegalStateException("Should never happen - primitive type is not a primitive?");
            }
        } else {
            return targetType;
        }
    }
}