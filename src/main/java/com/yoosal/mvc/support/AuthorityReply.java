package com.yoosal.mvc.support;

/**
 * 权限判断的数据模型
 */
public class AuthorityReply {
    private Class clazz;
    private String controllerName;
    private String invokeName;
    private String methodName;
    private Object object;
    private String message;
    private boolean canExecute = true;

    public AuthorityReply(Class clazz, String controllerName, String methodName, String invokeName, Object object) {
        this.clazz = clazz;
        this.controllerName = controllerName;
        this.methodName = methodName;
        this.invokeName = invokeName;
        this.object = object;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public Object getObject() {
        return object;
    }

    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }

    public boolean isCanExecute() {
        return canExecute;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
