package com.yoosal.mvc;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class SpringEntryPointManager extends EntryPointManager implements InitializingBean, DisposableBean {
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
    private String isRestful;

    public void setWritePath(String writePath) {
        this.writePath = writePath;
        this.setProperty(EntryPointManager.KEY_WRITE_PATH, writePath);
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
        this.setProperty(EntryPointManager.KEY_API_PREFIX, writePath);
    }

    public void setIsDebugger(String isDebugger) {
        this.isDebugger = isDebugger;
        this.setProperty(EntryPointManager.KEY_DEBUGGER, writePath);
    }

    public void setFormatException(String formatException) {
        this.formatException = formatException;
        this.setProperty(EntryPointManager.KEY_FORMAT_EXCEPTION, writePath);
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        this.setProperty(EntryPointManager.KEY_SCAN_PACKAGE, writePath);
    }

    public void setApiController(List apiController) {
        this.apiController = apiController;
        this.setClassesInstanceFromProperties(this.apiController);
    }

    public void setMethodParamName(String methodParamName) {
        this.methodParamName = methodParamName;
        this.setProperty(EntryPointManager.KEY_METHOD_KEY, methodParamName);
    }

    public void setClassParamName(String classParamName) {
        this.classParamName = classParamName;
        this.setProperty(EntryPointManager.KEY_CLASS_KEY, classParamName);
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        this.setProperty(EntryPointManager.KEY_REQUEST_URI, requestUri);
    }

    public void setIsCompressorJs(String isCompressorJs) {
        this.isCompressorJs = isCompressorJs;
        this.setProperty(EntryPointManager.KEY_COMPRESSOR_JS, isCompressorJs);
    }

    public void setIsRestful(String isRestful) {
        this.isRestful = isRestful;
        this.setProperty(EntryPointManager.KEY_REQUEST_RESTFUL, isRestful);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setScanClassAndInstance();
    }

    @Override
    public void destroy() throws Exception {
        // TODO: 2016/5/19 Bean销毁时处理
    }
}
