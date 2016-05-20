package com.yoosal.mvc.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class HttpSceneSupport extends AbstractSceneSupport {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Map<String, String[]> params;
    private Map<Class, Object> penetrate;

    public HttpSceneSupport(HttpServletRequest request, HttpServletResponse response, ControllerMethodParse controllerClassSupport) {
        super(controllerClassSupport);
        this.request = request;
        this.response = response;
        this.params = request.getParameterMap();
        penetrate.put(request.getClass(), request);
        penetrate.put(response.getClass(), response);
    }

    @Override
    public Map<String, String[]> getParams() {
        return params;
    }

    @Override
    public Map<Class, Object> getPenetrate() {
        return penetrate;
    }
}