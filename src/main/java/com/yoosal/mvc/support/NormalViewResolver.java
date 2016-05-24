package com.yoosal.mvc.support;

import com.yoosal.mvc.SceneFactory;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NormalViewResolver implements ViewResolver {
    @Override
    public void resolver(HttpServletRequest request, HttpServletResponse response) throws SceneInvokeException, ViewResolverException {
        SceneSupport sceneSupport = SceneFactory.createHttpScene(request, response);
        SceneView sceneView = new SceneView();
        sceneView.setRequest(request);
        sceneView.setResponse(response);
        sceneSupport.addParam(SceneView.class, sceneView);
        Object o = sceneSupport.invoke();
        this.resolverInvoke(request, response, sceneSupport, o);
    }

    private void resolverInvoke(HttpServletRequest request, HttpServletResponse response, SceneSupport sceneSupport, Object o) throws ViewResolverException {
        try {
            if (o.getClass().isAssignableFrom(String.class)) {
                String string = (String) o;
                if (string.startsWith("forward:")) {
                    this.forward(request, response, string.replace("forward:", ""));
                    return;
                } else if (string.startsWith("redirect:")) {
                    this.redirect(request, response, string.replace("redirect:", ""));
                    return;
                }
            }
            if (o.getClass().isAssignableFrom(SceneView.class)) {
                SceneView sceneView = (SceneView) o;
                if (sceneView.isForward()) {
                    this.forward(request, response, sceneView.getForward());
                    return;
                } else if (sceneView.isRedirect()) {
                    this.redirect(request, response, sceneView.getRedirect());
                    return;
                } else {
                    response.getWriter().write(sceneSupport.serialize(sceneView.getObject()));
                }
            } else {
                response.getWriter().write(sceneSupport.serialize(o));
            }
        } catch (Exception e) {
            throw new ViewResolverException("processing response exception", e);
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
        dispatcher.forward(request, response);
    }

    private void redirect(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
        response.sendRedirect(uri);
    }
}
