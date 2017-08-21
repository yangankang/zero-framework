package com.yoosal.mvc.support;

import com.yoosal.asm.*;
import com.yoosal.asm.Type;
import com.yoosal.common.ClassUtils;
import com.yoosal.common.NumberUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.json.JSON;
import com.yoosal.json.JSONException;
import com.yoosal.json.JSONObject;
import com.yoosal.mvc.convert.ConversionService;
import com.yoosal.mvc.convert.service.DefaultConversionService;
import com.yoosal.orm.ModelObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.*;

public class DefaultEmerge implements Emerge {
    private ConversionService conversionService = new DefaultConversionService();

    public Object getStringToObject(Class s, java.lang.reflect.Type type, Object[] object, Map<Class, Object> penetrate) throws ClassNotFoundException {

        for (Class key : penetrate.keySet()) {
            if (s.isAssignableFrom(key)) {
                return penetrate.get(key);
            }
        }

        if (object == null) return null;
        if (s.isArray()) {
            String shortName = s.getName().substring("[L".length(), s.getName().length() - 1);
            Class typeLoader = ClassUtils.getDefaultClassLoader().loadClass(shortName);
            Object arrayObj = Array.newInstance(typeLoader, object.length);
            int i = 0;
            for (Object o : object) {
                Object robject = this.getStringToObject(typeLoader, type, new Object[]{o}, penetrate);
                Array.set(arrayObj, i, robject);
                i++;
            }
            return arrayObj;
        }
        if (ClassUtils.isPrimitiveOrWrapper(s)
                || s.isAssignableFrom(String.class)
                || Date.class.isAssignableFrom(s)) {
            String o = String.valueOf(object[0]);
            if (ClassUtils.isNumberClass(s)) {
                if (object[0].equals("null") || object[0].equals("")) {
                    o = "0";
                }
            }
            return conversionService.executeConversion(o, s);
        } else {
            String obj = (String) object[0];

            if (Collection.class.isAssignableFrom(s)) {
                //simple Implementation
                try {
                    List list = objToList(obj, type);
                    return list;
                } catch (Exception e) {
                    throw new ClassCastException("action parameter case to " + s.getName() + " error.");
                }
            } else if (Map.class.isAssignableFrom(s)) {
                //simple Implementation
                try {
                    Map map = objToMap(obj, type);
                    return map;
                } catch (Exception e) {
                    throw new ClassCastException("request parameter case to " + s.getName() + " error by " + object[0]);
                }
            } else {

                try {
                    return JSON.toJavaObject(JSON.parseObject(obj), s);
                } catch (JSONException e) {
                    return null;
                }
            }
        }
    }

    private List objToList(String obj, java.lang.reflect.Type type) {
        List list = ModelObject.parseArray(obj);
        if (type instanceof ParameterizedType) {
            if (!type.getClass().isAssignableFrom(Map.class)) {
                List l = new ArrayList();
                for (Object object : list) {
                    java.lang.reflect.Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                    l.add(this.objToJavaObject((Class) params[0], object));
                }
                if (l.size() > 0) {
                    list = l;
                }
            }
        }
        return list;
    }

    private Map objToMap(String obj, java.lang.reflect.Type type) {
        Map map = ModelObject.parseObject(obj);
        if (type instanceof ParameterizedType) {
            java.lang.reflect.Type[] params = ((ParameterizedType) type).getActualTypeArguments();

            if (params.length > 0) {
                Map nm = new HashMap();
                for (Object o : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    nm.put(this.objToJavaObject((Class) params[0], entry.getKey()),
                            this.objToJavaObject((Class) params[1], entry.getValue()));
                }
                map = nm;
            }
        }

        return map;
    }

    private Object objToJavaObject(Class s, Object object) {
        if (ClassUtils.isPrimitiveOrWrapper(s)
                || s.isAssignableFrom(String.class)
                || Date.class.isAssignableFrom(s)) {
            String o = String.valueOf(object);
            if (ClassUtils.isNumberClass(s)) {
                if (StringUtils.isBlank(o)) {
                    o = "0";
                }
            }
            return conversionService.executeConversion(o, s);
        } else {
            return JSONObject.toJavaObject((JSON) object, s);
        }
    }

    @Override
    public Object[] getAssignment(String[] javaMethodParamNames, Method method, Map<String, String[]> paramFromRequest, Map<Class, Object> penetrate) throws ClassNotFoundException {
        Class[] types = method.getParameterTypes();
        java.lang.reflect.Type[] genericParameterTypes = method.getGenericParameterTypes();
        Object[] objects = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            Class o = types[i];
            objects[i] = getStringToObject(o, genericParameterTypes[i], paramFromRequest.get(javaMethodParamNames[i]), penetrate);
        }
        return objects;
    }

    @Override
    public String[] getMethodParamNames(final Method m) {
        final String[] paramNames = new String[m.getParameterTypes().length];
        final String n = m.getDeclaringClass().getName();
        ClassReader cr = null;
        try {
            cr = new ClassReader(n);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(final int access,
                                             final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // 方法名相同并且参数个数相同
                if (!name.equals(m.getName())
                        || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature,
                            exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc,
                        signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, v) {
                    @Override
                    public void visitLocalVariable(String name, String desc,
                                                   String signature, Label start, Label end, int index) {
                        int i = index - 1;
                        // 如果是静态方法，则第一就是参数
                        // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                        if (Modifier.isStatic(m.getModifiers())) {
                            i = index;
                        }
                        if (i >= 0 && i < paramNames.length) {
                            paramNames[i] = name;
                        }
                        super.visitLocalVariable(name, desc, signature, start,
                                end, index);
                    }

                };
            }
        }, 0);
        return paramNames;
    }

    private boolean sameType(Type[] types, Class<?>[] clazzes) {
        // 个数不同
        if (types.length != clazzes.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }
}
