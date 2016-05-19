package com.yoosal.mvc.support;

import java.lang.reflect.Method;

public class ControllerSupportModel {
    private Class clazz;
    private Object instance;
    private Method method;
    private String invokeName;
    private String controllerName;
    private String[] javaMethodParamNames;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public String[] getJavaMethodParamNames() {
        return javaMethodParamNames;
    }

    public void setJavaMethodParamNames(String[] javaMethodParamNames) {
        this.javaMethodParamNames = javaMethodParamNames;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public void setInvokeName(String invokeName) {
        this.invokeName = invokeName;
    }

    public ControllerSupportModel clone() {
        ControllerSupportModel classSupport = new ControllerSupportModel();
        classSupport.setClazz(clazz);
        classSupport.setInstance(instance);
        classSupport.setMethod(method);
        classSupport.setInvokeName(invokeName);
        classSupport.setControllerName(controllerName);
        classSupport.setJavaMethodParamNames(javaMethodParamNames);
        return classSupport;
    }
}
