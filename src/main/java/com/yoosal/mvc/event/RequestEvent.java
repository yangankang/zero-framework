package com.yoosal.mvc.event;

import com.yoosal.common.event.Event;

import javax.servlet.http.HttpServletRequest;

public class RequestEvent extends Event {

    private boolean isBefore = true;
    private String controller;
    private String method;
    private HttpServletRequest request;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RequestEvent(Object source) {
        super(source);
    }

    @Override
    public Object getType() {
        return MVCEventType.REQUEST_EVERY_TIME;
    }

    public void setBefore(boolean before) {
        isBefore = before;
    }

    public boolean isBefore() {
        return isBefore;
    }
}
