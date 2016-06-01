package com.yoosal.common;


import java.beans.Introspector;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.*;

public abstract class ClassUtils {
    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char INNER_CLASS_SEPARATOR = '$';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    public static final String CLASS_FILE_SUFFIX = ".class";
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap(32);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        Iterator primitiveType = primitiveWrapperTypeMap.entrySet().iterator();

        while (primitiveType.hasNext()) {
            Map.Entry primitiveTypes = (Map.Entry) primitiveType.next();
            primitiveTypeToWrapperMap.put((Class) primitiveTypes.getValue(), (Class) primitiveTypes.getKey());
            registerCommonClasses(new Class[]{(Class) primitiveTypes.getKey()});
        }

        HashSet primitiveTypes1 = new HashSet(32);
        primitiveTypes1.addAll(primitiveWrapperTypeMap.values());
        primitiveTypes1.addAll((Collection) Arrays.asList(new Class[]{boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class}));
        primitiveTypes1.add(Void.TYPE);
        Iterator var2 = primitiveTypes1.iterator();

        while (var2.hasNext()) {
            Class primitiveType1 = (Class) var2.next();
            primitiveTypeNameMap.put(primitiveType1.getName(), primitiveType1);
        }

        registerCommonClasses(new Class[]{Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class});
        registerCommonClasses(new Class[]{Number.class, Number[].class, String.class, String[].class, Object.class, Object[].class, Class.class, Class[].class});
        registerCommonClasses(new Class[]{Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class});
    }

    public ClassUtils() {
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        Class[] var4 = commonClasses;
        int var3 = commonClasses.length;

        for (int var2 = 0; var2 < var3; ++var2) {
            Class clazz = var4[var2];
            commonClassCache.put(clazz.getName(), clazz);
        }

    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var1) {
            ;
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }

        return cl;
    }

    public static ClassLoader overrideThreadContextClassLoader(ClassLoader classLoaderToUse) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
            currentThread.setContextClassLoader(classLoaderToUse);
            return threadContextClassLoader;
        } else {
            return null;
        }
    }

    @Deprecated
    public static Class<?> forName(String name) throws ClassNotFoundException, LinkageError {
        return forName(name, getDefaultClassLoader());
    }

    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = (Class) commonClassCache.get(name);
        }

        if (clazz != null) {
            return clazz;
        } else {
            Class ex;
            String classLoaderToUse1;
            if (name.endsWith("[]")) {
                classLoaderToUse1 = name.substring(0, name.length() - "[]".length());
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else if (name.startsWith("[L") && name.endsWith(";")) {
                classLoaderToUse1 = name.substring("[L".length(), name.length() - 1);
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else if (name.startsWith("[")) {
                classLoaderToUse1 = name.substring("[".length());
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else {
                ClassLoader classLoaderToUse = classLoader;
                if (classLoader == null) {
                    classLoaderToUse = getDefaultClassLoader();
                }

                try {
                    return classLoaderToUse.loadClass(name);
                } catch (ClassNotFoundException var8) {
                    int lastDotIndex = name.lastIndexOf(46);
                    if (lastDotIndex != -1) {
                        String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);

                        try {
                            return classLoaderToUse.loadClass(innerClassName);
                        } catch (ClassNotFoundException var7) {
                        }
                    }

                    throw var8;
                }
            }
        }
    }

    public static Class<?> resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        } catch (ClassNotFoundException var3) {
            throw new IllegalArgumentException("Cannot find class [" + className + "]", var3);
        } catch (LinkageError var4) {
            throw new IllegalArgumentException("Error loading class [" + className + "]: problem with class file or dependent class.", var4);
        }
    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class result = null;
        if (name != null && name.length() <= 8) {
            result = (Class) primitiveTypeNameMap.get(name);
        }

        return result;
    }

    @Deprecated
    public static boolean isPresent(String className) {
        return isPresent(className, getDefaultClassLoader());
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (Throwable var2) {
            return false;
        }
    }

    public static Class<?> getUserClass(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getUserClass((Class) instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz != null && clazz.getName().contains("$$")) {
            Class superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return superClass;
            }
        }

        return clazz;
    }

    public static boolean isCacheSafe(Class<?> clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        ClassLoader target = clazz.getClassLoader();
        if (target == null) {
            return false;
        } else {
            ClassLoader cur = classLoader;
            if (classLoader == target) {
                return true;
            } else {
                while (cur != null) {
                    cur = cur.getParent();
                    if (cur == target) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public static String getShortName(String className) {
        Assert.hasLength(className, "Class name must not be empty");
        int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }

        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        return shortName;
    }

    public static String getShortName(Class<?> clazz) {
        return getShortName((String) getQualifiedName(clazz));
    }

    public static String getShortNameAsProperty(Class<?> clazz) {
        String shortName = getShortName((Class) clazz);
        int dotIndex = shortName.lastIndexOf(46);
        shortName = dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName;
        return Introspector.decapitalize(shortName);
    }

    public static String getClassFileName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(46);
        return className.substring(lastDotIndex + 1) + ".class";
    }

    public static String getPackageName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(46);
        return lastDotIndex != -1 ? className.substring(0, lastDotIndex) : "";
    }

    public static String getQualifiedName(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() ? getQualifiedNameForArray(clazz) : clazz.getName();
    }

    private static String getQualifiedNameForArray(Class<?> clazz) {
        StringBuilder result = new StringBuilder();

        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            result.append("[]");
        }

        result.insert(0, clazz.getName());
        return result.toString();
    }

    public static String getQualifiedMethodName(Method method) {
        Assert.notNull(method, "Method must not be null");
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    public static String getDescriptiveType(Object value) {
        if (value == null) {
            return null;
        } else {
            Class clazz = value.getClass();
            if (Proxy.isProxyClass(clazz)) {
                StringBuilder result = new StringBuilder(clazz.getName());
                result.append(" implementing ");
                Class[] ifcs = clazz.getInterfaces();

                for (int i = 0; i < ifcs.length; ++i) {
                    result.append(ifcs[i].getName());
                    if (i < ifcs.length - 1) {
                        result.append(',');
                    }
                }

                return result.toString();
            } else {
                return clazz.isArray() ? getQualifiedNameForArray(clazz) : clazz.getName();
            }
        }
    }

    public static boolean matchesTypeName(Class<?> clazz, String typeName) {
        return typeName != null && (typeName.equals(clazz.getName()) || typeName.equals(clazz.getSimpleName()) || clazz.isArray() && typeName.equals(getQualifiedNameForArray(clazz)));
    }

    public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
        return getConstructorIfAvailable(clazz, paramTypes) != null;
    }

    public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");

        try {
            return clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException var2) {
            return null;
        }
    }

    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        return getMethodIfAvailable(clazz, methodName, paramTypes) != null;
    }

    public static Method getMethodIfAvailable(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");

        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException var3) {
            return null;
        }
    }

    public static int getMethodCountForName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        int count = 0;
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] var7 = declaredMethods;
        int var6 = declaredMethods.length;

        for (int ifc = 0; ifc < var6; ++ifc) {
            Method ifcs = var7[ifc];
            if (methodName.equals(ifcs.getName())) {
                ++count;
            }
        }

        Class[] var9 = clazz.getInterfaces();
        Class[] var8 = var9;
        int var11 = var9.length;

        for (var6 = 0; var6 < var11; ++var6) {
            Class var10 = var8[var6];
            count += getMethodCountForName(var10, methodName);
        }

        if (clazz.getSuperclass() != null) {
            count += getMethodCountForName(clazz.getSuperclass(), methodName);
        }

        return count;
    }

    public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        Method[] declaredMethods = clazz.getDeclaredMethods();
        Method[] var6 = declaredMethods;
        int var5 = declaredMethods.length;

        for (int ifc = 0; ifc < var5; ++ifc) {
            Method ifcs = var6[ifc];
            if (ifcs.getName().equals(methodName)) {
                return true;
            }
        }

        Class[] var8 = clazz.getInterfaces();
        Class[] var7 = var8;
        int var10 = var8.length;

        for (var5 = 0; var5 < var10; ++var5) {
            Class var9 = var7[var5];
            if (hasAtLeastOneMethodWithName(var9, methodName)) {
                return true;
            }
        }

        return clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName);
    }

    public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
        Method specificMethod = null;
        if (method != null && isOverridable(method, targetClass) && targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
            specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
        }

        return specificMethod != null ? specificMethod : method;
    }

    private static boolean isOverridable(Method method, Class targetClass) {
        return Modifier.isPrivate(method.getModifiers()) ? false : (!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()) ? getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass)) : true);
    }

    public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");

        try {
            Method method = clazz.getMethod(methodName, args);
            return Modifier.isStatic(method.getModifiers()) ? method : null;
        } catch (NoSuchMethodException var4) {
            return null;
        }
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isPrimitiveArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && clazz.getComponentType().isPrimitive();
    }

    public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType());
    }

    public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() && clazz != Void.TYPE ? (Class) primitiveTypeToWrapperMap.get(clazz) : clazz;
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        } else {
            Class resolvedWrapper;
            if (lhsType.isPrimitive()) {
                resolvedWrapper = (Class) primitiveWrapperTypeMap.get(rhsType);
                if (resolvedWrapper != null && lhsType.equals(resolvedWrapper)) {
                    return true;
                }
            } else {
                resolvedWrapper = (Class) primitiveTypeToWrapperMap.get(rhsType);
                if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isAssignableValue(Class<?> type, Object value) {
        Assert.notNull(type, "Type must not be null");
        return value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive();
    }

    public static String convertResourcePathToClassName(String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace('/', '.');
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace('.', '/');
    }

    public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
        Assert.notNull(resourceName, "Resource name must not be null");
        return !resourceName.startsWith("/") ? classPackageAsResourcePath(clazz) + "/" + resourceName : classPackageAsResourcePath(clazz) + resourceName;
    }

    public static String classPackageAsResourcePath(Class<?> clazz) {
        if (clazz == null) {
            return "";
        } else {
            String className = clazz.getName();
            int packageEndIndex = className.lastIndexOf(46);
            if (packageEndIndex == -1) {
                return "";
            } else {
                String packageName = className.substring(0, packageEndIndex);
                return packageName.replace('.', '/');
            }
        }
    }

    public static String classNamesToString(Class... classes) {
        return classNamesToString((Collection) ((Collection) Arrays.asList(classes)));
    }

    public static String classNamesToString(Collection<Class> classes) {
        if (CollectionUtils.isEmpty(classes)) {
            return "[]";
        } else {
            StringBuilder sb = new StringBuilder("[");
            Iterator it = classes.iterator();

            while (it.hasNext()) {
                Class clazz = (Class) it.next();
                sb.append(clazz.getName());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }

            sb.append("]");
            return sb.toString();
        }
    }

    public static Class[] getAllInterfaces(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClass(instance.getClass());
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
        return getAllInterfacesForClass(clazz, (ClassLoader) null);
    }

    public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, ClassLoader classLoader) {
        Set ifcs = getAllInterfacesForClassAsSet(clazz, classLoader);
        return (Class[]) ifcs.toArray(new Class[ifcs.size()]);
    }

    public static Set<Class> getAllInterfacesAsSet(Object instance) {
        Assert.notNull(instance, "Instance must not be null");
        return getAllInterfacesForClassAsSet(instance.getClass());
    }

    public static Set<Class> getAllInterfacesForClassAsSet(Class clazz) {
        return getAllInterfacesForClassAsSet(clazz, (ClassLoader) null);
    }

    public static Set<Class> getAllInterfacesForClassAsSet(Class clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        } else {
            LinkedHashSet interfaces;
            for (interfaces = new LinkedHashSet(); clazz != null; clazz = clazz.getSuperclass()) {
                Class[] ifcs = clazz.getInterfaces();
                Class[] var7 = ifcs;
                int var6 = ifcs.length;

                for (int var5 = 0; var5 < var6; ++var5) {
                    Class ifc = var7[var5];
                    interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
                }
            }

            return interfaces;
        }
    }

    public static Class<?> createCompositeInterface(Class<?>[] interfaces, ClassLoader classLoader) {
        Assert.notEmpty(interfaces, "Interfaces must not be empty");
        Assert.notNull(classLoader, "ClassLoader must not be null");
        return Proxy.getProxyClass(classLoader, interfaces);
    }

    public static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        } else {
            try {
                Class actualClass = classLoader.loadClass(clazz.getName());
                return clazz == actualClass;
            } catch (ClassNotFoundException var3) {
                return false;
            }
        }
    }

    public static InputStream getResourceAsStream(String name) {

        InputStream is = ClassLoader.getSystemResourceAsStream(name.replace('.', '/')
                + ".class");
        if (is == null) {
            is = ClassUtils.getDefaultClassLoader().getResourceAsStream(name.replace('.', '/')
                    + ".class");
        }
        return is;
    }

    public static boolean isNumberClass(Class clazz) {
        if (clazz.isAssignableFrom(int.class) ||
                clazz.isAssignableFrom(long.class) ||
                clazz.isAssignableFrom(float.class) ||
                clazz.isAssignableFrom(double.class) ||
                clazz.isAssignableFrom(Integer.class) ||
                clazz.isAssignableFrom(Long.class) ||
                clazz.isAssignableFrom(Float.class) ||
                clazz.isAssignableFrom(Double.class)) {
            return true;
        }
        return false;
    }
}
