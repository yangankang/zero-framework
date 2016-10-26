package com.yoosal.mvc.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpSceneSupport extends AbstractSceneSupport {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Map<String, String[]> params = new HashMap<String, String[]>();
    private Map<Class, Object> penetrate = new HashMap<Class, Object>();

    public HttpSceneSupport(HttpServletRequest request, HttpServletResponse response, ControllerMethodParse controllerClassSupport) {
        super(controllerClassSupport);
        this.request = request;
        this.response = response;
        this.params = request.getParameterMap();
        penetrate.put(request.getClass(), request);
        penetrate.put(response.getClass(), response);

        penetrate.put(HttpServletRequest.class, request);
        penetrate.put(HttpServletResponse.class, response);
    }

    @Override
    public Map<String, String[]> getParams() {
        return params;
    }

    @Override
    public Map<Class, Object> getPenetrate() {
        return penetrate;
    }

    @Override
    public void addParam(String key, String[] values) {
        params.put(key, values);
    }

    @Override
    public void addParam(Class clazz, Object obj) {
        penetrate.put(clazz, obj);
    }
}
