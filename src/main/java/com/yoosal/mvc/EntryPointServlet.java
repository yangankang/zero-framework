package com.yoosal.mvc;

import com.yoosal.common.StringUtils;
import com.yoosal.mvc.exception.InitializeSceneException;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * MVC的入口Servlet,这里初始化整个配置,并且所有的MVC请求都要经过这里
 */
public class EntryPointServlet extends HttpServlet {
    /**
     * 这里如果不配置可以交给spring类初始化配置SpringEntryPointManager
     */
    private static final EntryPointManager pointManager = new EntryPointManager();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //这里使用GET/POST的传参方式，还有一种是Restful方式
            String classNameFromParam = req.getParameter(EntryPointManager.getClassKey());
            String methodNameFromParam = req.getParameter(EntryPointManager.getMethodKey());
            EntryPointManager.getViewResolver().resolver(req, resp, classNameFromParam, methodNameFromParam);
        } catch (SceneInvokeException e) {
            e.printStackTrace();
        } catch (ViewResolverException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String frameworkConfigLocation = config.getInitParameter("frameworkConfigLocation");
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(frameworkConfigLocation)) {
            try {
                properties.load(new FileInputStream(frameworkConfigLocation));
                pointManager.setProperties(properties);
            } catch (Exception e) {
                throw new InitializeSceneException("initialize by properties failed", e);
            }
        }
    }
}
