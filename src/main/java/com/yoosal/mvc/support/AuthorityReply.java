package com.yoosal.mvc.support;

import javax.servlet.http.HttpServletRequest;

public class AuthorityReply {
    private Class clazz;
    private String controllerName;
    private String invokeName;
    private Object object;
    private String message;
    private boolean canExecute;

    public AuthorityReply(Class clazz, String controllerName, String invokeName, Object object) {
        this.clazz = clazz;
        this.controllerName = controllerName;
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
}
