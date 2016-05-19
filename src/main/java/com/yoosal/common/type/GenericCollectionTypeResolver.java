package com.yoosal.common.type;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;

public abstract class GenericCollectionTypeResolver {

    public static Class<?> getCollectionType(Class<? extends Collection> collectionClass) {
        return extractTypeFromClass(collectionClass, Collection.class, 0);
    }

    public static Class<?> getMapKeyType(Class<? extends Map> mapClass) {
        return extractTypeFromClass(mapClass, Map.class, 0);
    }

    public static Class<?> getMapValueType(Class<? extends Map> mapClass) {
        return extractTypeFromClass(mapClass, Map.class, 1);
    }

    public static Class<?> getCollectionFieldType(Field collectionField) {
        return getGenericFieldType(collectionField, Collection.class, 0, 1);
    }

    public static Class<?> getCollectionFieldType(Field collectionField, int nestingLevel) {
        return getGenericFieldType(collectionField, Collection.class, 0, nestingLevel);
    }

    public static Class<?> getMapKeyFieldType(Field mapField) {
        return getGenericFieldType(mapField, Map.class, 0, 1);
    }

    public static Class<?> getMapKeyFieldType(Field mapField, int nestingLevel) {
        return getGenericFieldType(mapField, Map.class, 0, nestingLevel);
    }

    public static Class<?> getMapValueFieldType(Field mapField) {
        return getGenericFieldType(mapField, Map.class, 1, 1);
    }

    public static Class<?> getMapValueFieldType(Field mapField, int nestingLevel) {
        return getGenericFieldType(mapField, Map.class, 1, nestingLevel);
    }

    public static Class<?> getCollectionParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Collection.class, 0);
    }

    public static Class<?> getMapKeyParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Map.class, 0);
    }

    public static Class<?> getMapValueParameterType(MethodParameter methodParam) {
        return getGenericParameterType(methodParam, Map.class, 1);
    }

    public static Class<?> getCollectionReturnType(Method method) {
        return getGenericReturnType(method, Collection.class, 0, 1);
    }

    public static Class<?> getCollectionReturnType(Method method, int nestingLevel) {
        return getGenericReturnType(method, Collection.class, 0, nestingLevel);
    }

    public static Class<?> getMapKeyReturnType(Method method) {
        return getGenericReturnType(method, Map.class, 0, 1);
    }

    public static Class<?> getMapKeyReturnType(Method method, int nestingLevel) {
        return getGenericReturnType(method, Map.class, 0, nestingLevel);
    }

    public static Class<?> getMapValueReturnType(Method method) {
        return getGenericReturnType(method, Map.class, 1, 1);
    }

    public static Class<?> getMapValueReturnType(Method method, int nestingLevel) {
        return getGenericReturnType(method, Map.class, 1, nestingLevel);
    }

    private static Class<?> getGenericParameterType(MethodParameter methodParam, Class<?> source, int typeIndex) {
        return extractType(methodParam, GenericTypeResolver.getTargetType(methodParam),
                source, typeIndex, methodParam.getNestingLevel(), 1);
    }

    private static Class<?> getGenericFieldType(Field field, Class<?> source, int typeIndex, int nestingLevel) {
        return extractType(null, field.getGenericType(), source, typeIndex, nestingLevel, 1);
    }

    private static Class<?> getGenericReturnType(Method method, Class<?> source, int typeIndex, int nestingLevel) {
        return extractType(null, method.getGenericReturnType(), source, typeIndex, nestingLevel, 1);
    }

    private static Class<?> extractType(
            MethodParameter methodParam, Type type, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        Type resolvedType = type;
        if (type instanceof TypeVariable && methodParam != null && methodParam.typeVariableMap != null) {
            Type mappedType = methodParam.typeVariableMap.get((TypeVariable) type);
            if (mappedType != null) {
                resolvedType = mappedType;
            }
        }
        if (resolvedType instanceof ParameterizedType) {
            return extractTypeFromParameterizedType(
                    methodParam, (ParameterizedType) resolvedType, source, typeIndex, nestingLevel, currentLevel);
        } else if (resolvedType instanceof Class) {
            return extractTypeFromClass(methodParam, (Class) resolvedType, source, typeIndex, nestingLevel, currentLevel);
        } else {
            return null;
        }
    }

    private static Class<?> extractTypeFromParameterizedType(MethodParameter methodParam,
                                                             ParameterizedType ptype, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        if (!(ptype.getRawType() instanceof Class)) {
            return null;
        }
        Class rawType = (Class) ptype.getRawType();
        Type[] paramTypes = ptype.getActualTypeArguments();
        if (nestingLevel - currentLevel > 0) {
            int nextLevel = currentLevel + 1;
            Integer currentTypeIndex = (methodParam != null ? methodParam.getTypeIndexForLevel(nextLevel) : null);
            // Default is last parameter type: Collection element or Map value.
            int indexToUse = (currentTypeIndex != null ? currentTypeIndex : paramTypes.length - 1);
            Type paramType = paramTypes[indexToUse];
            return extractType(methodParam, paramType, source, typeIndex, nestingLevel, nextLevel);
        }
        if (source != null && !source.isAssignableFrom(rawType)) {
            return null;
        }
        Class fromSuperclassOrInterface =
                extractTypeFromClass(methodParam, rawType, source, typeIndex, nestingLevel, currentLevel);
        if (fromSuperclassOrInterface != null) {
            return fromSuperclassOrInterface;
        }
        if (paramTypes == null || typeIndex >= paramTypes.length) {
            return null;
        }
        Type paramType = paramTypes[typeIndex];
        if (paramType instanceof TypeVariable && methodParam != null && methodParam.typeVariableMap != null) {
            Type mappedType = methodParam.typeVariableMap.get((TypeVariable) paramType);
            if (mappedType != null) {
                paramType = mappedType;
            }
        }
        if (paramType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) paramType;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds != null && upperBounds.length > 0 && !Object.class.equals(upperBounds[0])) {
                paramType = upperBounds[0];
            } else {
                Type[] lowerBounds = wildcardType.getLowerBounds();
                if (lowerBounds != null && lowerBounds.length > 0 && !Object.class.equals(lowerBounds[0])) {
                    paramType = lowerBounds[0];
                }
            }
        }
        if (paramType instanceof ParameterizedType) {
            paramType = ((ParameterizedType) paramType).getRawType();
        }
        if (paramType instanceof GenericArrayType) {
            // A generic array type... Let's turn it into a straight array type if possible.
            Type compType = ((GenericArrayType) paramType).getGenericComponentType();
            if (compType instanceof Class) {
                return Array.newInstance((Class) compType, 0).getClass();
            }
        } else if (paramType instanceof Class) {
            // We finally got a straight Class...
            return (Class) paramType;
        }
        return null;
    }

    private static Class<?> extractTypeFromClass(Class<?> clazz, Class<?> source, int typeIndex) {
        return extractTypeFromClass(null, clazz, source, typeIndex, 1, 1);
    }

    private static Class<?> extractTypeFromClass(
            MethodParameter methodParam, Class<?> clazz, Class<?> source, int typeIndex, int nestingLevel, int currentLevel) {

        if (clazz.getName().startsWith("java.util.")) {
            return null;
        }
        if (clazz.getSuperclass() != null && isIntrospectionCandidate(clazz.getSuperclass())) {
            return extractType(methodParam, clazz.getGenericSuperclass(), source, typeIndex, nestingLevel, currentLevel);
        }
        Type[] ifcs = clazz.getGenericInterfaces();
        if (ifcs != null) {
            for (Type ifc : ifcs) {
                Type rawType = ifc;
                if (ifc instanceof ParameterizedType) {
                    rawType = ((ParameterizedType) ifc).getRawType();
                }
                if (rawType instanceof Class && isIntrospectionCandidate((Class) rawType)) {
                    return extractType(methodParam, ifc, source, typeIndex, nestingLevel, currentLevel);
                }
            }
        }
        return null;
    }

    private static boolean isIntrospectionCandidate(Class clazz) {
        return (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz));
    }

}
