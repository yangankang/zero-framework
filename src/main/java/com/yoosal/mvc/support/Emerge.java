package com.yoosal.mvc.support;

import java.lang.reflect.Method;
import java.util.Map;

public interface Emerge {
    String[] getMethodParamNames(final Method m);

    Object getStringToObject(Class s, Object[] object, Map<Class, Object> penetrate) throws ClassNotFoundException;

    /**
     * @param javaClassParamNames 每个方法的参数名称，java方法中代码定义的名称
     * @param method              要执行的方法
     * @param paramFromRequest    请求的参数Map
     * @param penetrate           额外的参数实例，比如如果方法参数定义了HttpServletRequest那么通过这个传入
     * @return
     * @throws ClassNotFoundException
     */
    Object[] getAssignment(String[] javaClassParamNames, Method method, Map<String, String[]> paramFromRequest, Map<Class, Object> penetrate) throws ClassNotFoundException;
}
