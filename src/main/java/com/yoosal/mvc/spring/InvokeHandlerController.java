package com.yoosal.mvc.spring;

import com.yoosal.mvc.EntryPointManager;
import com.yoosal.mvc.exception.SceneInvokeException;
import com.yoosal.mvc.exception.ViewResolverException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InvokeHandlerController extends SimpleControllerHandlerAdapter implements Controller {
    public static final String SPRING_CONTROLLER_INVOKE_HANDLER = "_MVCEntryPointControllerHandler";

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


}
