package com.yoosal.mvc.spring;

import com.yoosal.common.Logger;
import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.SpringEntryPointManager;
import com.yoosal.mvc.exception.ParseTemplateException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OutJavaApiHandlerController extends SimpleControllerHandlerAdapter implements Controller, ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(InvokeHandlerController.class);

    @Autowired
    private SpringEntryPointManager springEntryPointManager;

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            response.setContentType("application/x-javascript;charset=UTF-8");
            EntryPointManager.produceJavaScriptMapping(response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseTemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String[] outApiControllerNames = applicationContext.getBeanNamesForType(OutJavaApiHandlerController.class);
        if (outApiControllerNames != null && outApiControllerNames.length > 0) {
            springEntryPointManager.setApiRequestUri(outApiControllerNames[0]);
            logger.info("完成JS映射Controller注入,访问地址是:" + outApiControllerNames[0]);
        }
    }
}
