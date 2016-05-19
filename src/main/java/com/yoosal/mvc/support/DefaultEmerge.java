package com.yoosal.mvc.support;

import com.yoosal.asm.*;
import com.yoosal.common.ClassUtils;
import com.yoosal.json.JSON;
import com.yoosal.json.JSONArray;
import com.yoosal.json.JSONException;
import com.yoosal.mvc.convert.ConversionService;
import com.yoosal.mvc.convert.service.DefaultConversionService;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultEmerge implements Emerge {
    private ConversionService conversionService = new DefaultConversionService();

    public Object getStringToObject(Class s, Object[] object, Map<Class, Object> penetrate) throws ClassNotFoundException {
        if (object == null) return null;
        if (s.isArray()) {
            String shortName = s.getName().substring("[L".length(), s.getName().length() - 1);
            Class typeLoader = ClassUtils.getDefaultClassLoader().loadClass(shortName);
            Object arrayObj = Array.newInstance(typeLoader, object.length);
            int i = 0;
            for (Object o : object) {
                Object robject = this.getStringToObject(typeLoader, new Object[]{o}, penetrate);
                Array.set(arrayObj, i, robject);
                i++;
            }
            return arrayObj;
        }
        if (ClassUtils.isPrimitiveOrWrapper(s) || s.isAssignableFrom(String.class)) {
            return conversionService.executeConversion(object[0], s);
        } else {
            if (Collection.class.isAssignableFrom(s)) {
                //simple Implementation
                try {
                    List list = JSON.parseArray(String.valueOf(object[0]));
                    return list;
                } catch (Exception e) {
                    throw new ClassCastException("action parameter case to " + s.getName() + " error.");
                }
            } else if (Map.class.isAssignableFrom(s)) {
                //simple Implementation
                try {
                    Map map = JSON.parseObject(String.valueOf(object[0]));
                    return map;
                } catch (Exception e) {
                    throw new ClassCastException("request parameter case to " + s.getName() + " error.");
                }
            } else {
                for (Class key : penetrate.keySet()) {
                    if (key.isAssignableFrom(s)) {
                        return penetrate.get(key);
                    }
                }
                try {
                    return JSON.toJavaObject(JSON.parseObject(String.valueOf(object[0])), s);
                } catch (JSONException e) {
                    return null;
                }
            }
        }
    }

    @Override
    public Object[] getAssignment(String[] javaMethodParamNames, Method method, Map<String, String[]> paramFromRequest, Map<Class, Object> penetrate) throws ClassNotFoundException {
        Class[] types = method.getParameterTypes();
        Object[] objects = new Object[types.length];
        int i = 0;
        for (Class o : types) {
            objects[i] = getStringToObject(o, paramFromRequest.get(javaMethodParamNames[i]), penetrate);
            i++;
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
