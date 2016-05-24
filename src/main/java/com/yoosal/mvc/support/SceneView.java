package com.yoosal.mvc.support;

import com.yoosal.common.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SceneView {
    public static final String SCENE_TYPE_REDIRECT = "redirect";
    public static final String SCENE_TYPE_FORWARD = "forward";
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String redirect;
    private String forward;
    private Object object;

    public SceneView(String uri, String type) {
        if (type.equalsIgnoreCase(SCENE_TYPE_FORWARD)) {
            this.forward = uri;
        }
        if (type.equalsIgnoreCase(SCENE_TYPE_REDIRECT)) {
            this.redirect = uri;
        }
    }

    public SceneView() {
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getForward() {
        return forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public void dontRedirect() {
        this.redirect = null;
    }

    public void dontForward() {
        this.forward = null;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isRedirect() {
        if (StringUtils.isNotBlank(redirect)) {
            return true;
        }
        return false;
    }

    public boolean isForward() {
        if (StringUtils.isNotBlank(forward)) {
            return true;
        }
        return false;
    }
}
