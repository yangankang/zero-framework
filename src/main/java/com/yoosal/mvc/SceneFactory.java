package com.yoosal.mvc;

import com.yoosal.common.AnnotationUtils;
import com.yoosal.common.Logger;
import com.yoosal.common.StringUtils;
import com.yoosal.mvc.annotation.APIController;
import com.yoosal.mvc.annotation.Printer;
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
    private static Logger logger = Logger.getLogger(SceneFactory.class);
    private static final Map<String, ControllerMethodParse> controllers = new HashMap<String, ControllerMethodParse>();
    private static final Emerge emerge = new DefaultEmerge();

    public static void cacheControllerInfo() {
        if (controllers != null && controllers.size() > 0) {
            return;
        }
        List objects = EntryPointManager.getApiClass();
        if (objects != null) {
            /**
             * 将所有的配置的类的信息缓存到Map中去
             */
            for (Object object : objects) {
                List<ControllerMethodParse> classSupports = createSupportFromObject(object);
                if (classSupports != null) {
                    for (ControllerMethodParse classSupport : classSupports) {
                        controllers.put(classSupport.getInvokeName().toLowerCase(), classSupport);
                    }
                }
            }
        }
    }

    public static List<ControllerMethodParse> getControllersInfo() {
        List<ControllerMethodParse> controllerMethodParses = new ArrayList<ControllerMethodParse>();
        for (String key : controllers.keySet()) {
            controllerMethodParses.add(controllers.get(key));
        }
        return controllerMethodParses;
    }

    private static List<ControllerMethodParse> createSupportFromObject(Object object) {
        ControllerMethodParse classSupport = new ControllerMethodParse();
        Class<?> clazz = object.getClass();
        classSupport.setClazz(clazz);
        classSupport.setInstance(object);
        //判断类中注解配置的名称
        Annotation clazzAnnotation = AnnotationUtils.findAnnotation(clazz, APIController.class);
        if (clazzAnnotation == null) return null;
        Object clazzAnnotationDefaultValue = AnnotationUtils.getDefaultValue(clazzAnnotation);
        if (clazzAnnotationDefaultValue != null) {
            String defaultValue = (String) clazzAnnotationDefaultValue;
            if (StringUtils.isNotBlank(defaultValue)) {
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
            Annotation annotation = AnnotationUtils.findAnnotation(method, Printer.class);
            if (annotation != null) {
                ControllerMethodParse methodClassSupport = classSupport.clone();
                methodClassSupport.setMethod(method);
                String invokeName = classSupport.getControllerName() + "." + method.getName();
                methodClassSupport.setInvokeName(invokeName);
                methodClassSupport.setMethodName(method.getName());
                //获得方法的参数代码定义的名称
                methodClassSupport.setJavaMethodParamNames(emerge.getMethodParamNames(method));
                classSupports.add(methodClassSupport);
            }
        }

        return classSupports;
    }

    public static SceneSupport createHttpScene(HttpServletRequest request, HttpServletResponse response,
                                               String className, String methodName) {
        String findMethodKey = className + "." + methodName;
        ControllerMethodParse classSupport = controllers.get(findMethodKey.toLowerCase());
        if (classSupport == null) {
            logger.info("没有相匹配的类或者方法:" + findMethodKey);
            return null;
        }
        return new HttpSceneSupport(request, response, classSupport);
    }
}
