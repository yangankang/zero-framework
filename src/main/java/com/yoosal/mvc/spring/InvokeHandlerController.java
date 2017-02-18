package com.yoosal.mvc.spring;

import com.yoosal.common.Logger;
import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.SpringEntryPointManager;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InvokeHandlerController extends SimpleControllerHandlerAdapter implements Controller, ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(InvokeHandlerController.class);

    @Autowired
    private SpringEntryPointManager springEntryPointManager;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            response.setContentType("text/html;charset=UTF-8");
            EntryPointManager.getViewResolver().resolver(request, response);
        } catch (SceneInvokeException e) {
            e.printStackTrace();
        } catch (ViewResolverException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] invokeControllerNames = applicationContext.getBeanNamesForType(InvokeHandlerController.class);
        if (invokeControllerNames != null && invokeControllerNames.length > 0) {
            springEntryPointManager.setRequestUri(invokeControllerNames[0]);
            logger.info("完成入口Controller注入,访问地址是:" + invokeControllerNames[0]);
        }
    }
}
