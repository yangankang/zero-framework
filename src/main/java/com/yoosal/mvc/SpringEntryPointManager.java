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

    public void setWritePath(String writePath) {
        this.writePath = writePath;
        this.setProperty(KEY_WRITE_PATH, writePath);
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
        this.setProperty(KEY_API_PREFIX, writePath);
    }

    public void setIsDebugger(String isDebugger) {
        this.isDebugger = isDebugger;
        this.setProperty(KEY_DEBUGGER, writePath);
    }

    public void setFormatException(String formatException) {
        this.formatException = formatException;
        this.setProperty(KEY_FORMAT_EXCEPTION, writePath);
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        this.setProperty(KEY_SCAN_PACKAGE, writePath);
    }

    public void setApiController(List apiController) {
        this.apiController = apiController;
        this.setClassesInstanceFromProperties(this.apiController);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initialize();
    }

    @Override
    public void destroy() throws Exception {
        super.deinitialize();
    }
}
