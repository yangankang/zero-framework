package com.yoosal.mvc.spring;

import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.ParseTemplateException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OutJavaApiHandlerController extends SimpleControllerHandlerAdapter implements Controller {
    public static final String SPRING_CONTROLLER_API_HANDLER = "_MVCEntryPointControllerApiHandler";

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
}
