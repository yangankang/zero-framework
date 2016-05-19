package com.yoosal.mvc;

import com.yoosal.mvc.exception.InitializeSceneException;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.support.SceneSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * MVC的入口Servlet,这里初始化整个配置,并且所有的MVC请求都要经过这里，由这里分发，当然这个入口不是唯一的
 * 入口还支持SpringMVC来分发地址
 */
public class EntryPointServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            SceneSupport sceneSupport = SceneFactory.createHttpScene(req, resp);
            Object object = sceneSupport.invoke();
            String serialize = sceneSupport.serialize(object);
            resp.getWriter().write(serialize);
        } catch (SceneInvokeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String frameworkConfigLocation = config.getInitParameter("frameworkConfigLocation");
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(frameworkConfigLocation));
            EntryPointManager.setProperties(properties);
        } catch (Exception e) {
            throw new InitializeSceneException("initialize by properties failed", e);
        }
    }
}
