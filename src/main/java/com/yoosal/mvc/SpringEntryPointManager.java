package com.yoosal.mvc;

import com.yoosal.mvc.support.SceneSupport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SpringEntryPointManager extends HandlerInterceptorAdapter implements InitializingBean, DisposableBean {
    private String writePath;
    private String apiPrefix;
    private String isDebugger;
    private String formatException;
    private String scanPackage;
    private List apiController;
    private String methodParamName;
    private String classParamName;
    private String requestUri;
    private String isCompressorJs;

    public void setWritePath(String writePath) {
        this.writePath = writePath;
        EntryPointManager.setProperty(EntryPointManager.KEY_WRITE_PATH, writePath);
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
        EntryPointManager.setProperty(EntryPointManager.KEY_API_PREFIX, writePath);
    }

    public void setIsDebugger(String isDebugger) {
        this.isDebugger = isDebugger;
        EntryPointManager.setProperty(EntryPointManager.KEY_DEBUGGER, writePath);
    }

    public void setFormatException(String formatException) {
        this.formatException = formatException;
        EntryPointManager.setProperty(EntryPointManager.KEY_FORMAT_EXCEPTION, writePath);
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        EntryPointManager.setProperty(EntryPointManager.KEY_SCAN_PACKAGE, writePath);
    }

    public void setApiController(List apiController) {
        this.apiController = apiController;
        EntryPointManager.setClassesInstanceFromProperties(this.apiController);
    }

    public void setMethodParamName(String methodParamName) {
        this.methodParamName = methodParamName;
        EntryPointManager.setProperty(EntryPointManager.KEY_METHOD_KEY, methodParamName);
    }

    public void setClassParamName(String classParamName) {
        this.classParamName = classParamName;
        EntryPointManager.setProperty(EntryPointManager.KEY_CLASS_KEY, classParamName);
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        EntryPointManager.setProperty(EntryPointManager.KEY_REQUEST_URI, requestUri);
    }

    public void setIsCompressorJs(String isCompressorJs) {
        this.isCompressorJs = isCompressorJs;
        EntryPointManager.setProperty(EntryPointManager.KEY_COMPRESSOR_JS, isCompressorJs);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String url = request.getRequestURL().toString();
        if (url.endsWith(this.requestUri) || url.matches(this.requestUri)) {
            EntryPointManager.getViewResolver().resolver(request, response);
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EntryPointManager.setScanClassAndInstance();
    }

    @Override
    public void destroy() throws Exception {
        // TODO: 2016/5/19 Bean销毁时处理
    }
}
