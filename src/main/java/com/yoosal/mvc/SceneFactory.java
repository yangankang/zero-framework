package com.yoosal.mvc;

import com.yoosal.common.AnnotationUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.mvc.annotation.APIController;
import com.yoosal.mvc.annotation.OutMethod;
import com.yoosal.mvc.exception.InitializeSceneException;
import com.yoosal.mvc.support.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SceneFactory {

    private static final Map<String, ControllerSupportModel> controllers = new HashMap<String, ControllerSupportModel>();
    private static final Emerge emerge = new DefaultEmerge();
    private static String KEY_METHOD_NAME = null;
    private static String KEY_CLASS_NAME = null;

    static {
        KEY_METHOD_NAME = EntryPointManager.getMethodKey();
        KEY_CLASS_NAME = EntryPointManager.getClassKey();

        List objects = EntryPointManager.getApiClass();
        if (objects != null) {
            /**
             * 将所有的配置的类的信息缓存到Map中去
             */
            for (Object object : objects) {
                List<ControllerSupportModel> classSupports = createSupportFromObject(object);
                if (classSupports != null) {
                    for (ControllerSupportModel classSupport : classSupports) {
                        controllers.put(classSupport.getControllerName().toLowerCase(), classSupport);
                    }
                }
            }
        }
    }

    private static List<ControllerSupportModel> createSupportFromObject(Object object) {
        ControllerSupportModel classSupport = new ControllerSupportModel();
        Class<?> clazz = object.getClass();
        classSupport.setClazz(clazz);
        classSupport.setInstance(object);
        //判断类中注解配置的名称
        Annotation clazzAnnotation = AnnotationUtils.findAnnotation(clazz, APIController.class);
        if (clazzAnnotation == null) return null;
        Object clazzAnnotationDefaultValue = AnnotationUtils.getDefaultValue(clazzAnnotation);
        if (clazzAnnotationDefaultValue != null) {
            String defaultValue = String.valueOf(clazzAnnotationDefaultValue);
            if (!defaultValue.isEmpty()) {
                classSupport.setControllerName(defaultValue);
            }
        }

        if (StringUtils.isBlank(classSupport.getControllerName())) {
            classSupport.setControllerName(clazz.getSimpleName());
        }

        List classSupports = new ArrayList();
        //对象表示的类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法。
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation annotation = AnnotationUtils.findAnnotation(method, OutMethod.class);
            if (annotation != null) {
                ControllerSupportModel methodClassSupport = classSupport.clone();
                methodClassSupport.setMethod(method);
                String invokeName = classSupport.getControllerName() + "." + method.getName();
                methodClassSupport.setInvokeName(invokeName);
                //获得方法的参数代码定义的名称
                methodClassSupport.setJavaMethodParamNames(emerge.getMethodParamNames(method));
                classSupports.add(methodClassSupport);
            }
        }

        return classSupports;
    }

    public static SceneSupport createHttpScene(HttpServletRequest request, HttpServletResponse response) {
        String findMethodKey = request.getParameter(KEY_CLASS_NAME) + "." + request.getParameter(KEY_METHOD_NAME);
        ControllerSupportModel classSupport = controllers.get(findMethodKey.toLowerCase());
        if (classSupport == null) {
            throw new InitializeSceneException("no class or method name " + findMethodKey);
        }
        return new HttpSceneSupport(request, response, classSupport);
    }
}
