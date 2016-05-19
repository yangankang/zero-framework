package com.yoosal.mvc.support;

import javax.servlet.http.HttpServletRequest;

public class AuthorityReply {
    private String clazz;
    private String controllerName;
    private String invokeName;
    private Object object;
    private HttpServletRequest request;
    private boolean canExecute;

    public String getClazz() {
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

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setCanExecute(boolean canExecute) {
        this.canExecute = canExecute;
    }

    public boolean isCanExecute() {
        return canExecute;
    }
}
