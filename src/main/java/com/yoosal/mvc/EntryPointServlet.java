package com.yoosal.mvc;

import com.yoosal.common.ResourceUtils;
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
import java.io.InputStream;
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
            EntryPointManager.getViewResolver().resolver(req, resp);
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
        InputStream propertiesInputStream;
        if (frameworkConfigLocation.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            propertiesInputStream = this.getClass().getResourceAsStream("/" + frameworkConfigLocation.split(":")[1]);
        } else {
            propertiesInputStream = config.getServletContext().getResourceAsStream(frameworkConfigLocation);
        }
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(frameworkConfigLocation)) {
            try {
                properties.load(propertiesInputStream);
                pointManager.setProperties(properties, config.getServletContext());
            } catch (Exception e) {
                throw new InitializeSceneException("initialize by properties failed", e);
            }
        }
    }
}
