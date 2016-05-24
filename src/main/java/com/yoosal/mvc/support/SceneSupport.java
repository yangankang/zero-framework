package com.yoosal.mvc.support;

import com.yoosal.mvc.exception.SceneInvokeException;

/**
 * 由SceneFactory类通过传入字段判断然后创建，这个接口直接和Servlet进行交互，
 */
public interface SceneSupport {
    Object invoke() throws SceneInvokeException;

    void setControllerClassSupport(ControllerMethodParse controllerClassSupport);

    ControllerMethodParse getControllerClassSupport();

    String serialize(Object object);

    void addParam(String key, String[] values);

    void addParam(Class clazz, Object obj);
}
