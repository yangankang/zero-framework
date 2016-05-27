package com.yoosal.mvc;

import com.yoosal.mvc.annotation.APIController;
import com.yoosal.mvc.support.AuthoritySupport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpringEntryPointManager extends EntryPointManager implements InitializingBean, DisposableBean, ServletContextAware, ApplicationContextAware {
    private ServletContext servletContext;
    private ApplicationContext applicationContext;
    /**
     * 是否扫描所有的Spring Bean获得已经实例化的APIController
     */
    private boolean scanSpringContext = false;

    private String writePath;
    private String apiPrefix;
    private String isDebugger;
    private String formatException;
    private String scanPackage;
    private Set<Object> apiController;
    private String methodParamName;
    private String classParamName;
    private String requestUri;
    private String isCompressorJs;
    private String isRestful;
    private AuthoritySupport authoritySupport;

    @Override
    public void setAuthoritySupport(AuthoritySupport authoritySupport) {
        this.authoritySupport = authoritySupport;
        super.setAuthoritySupport(authoritySupport);
    }

    public void setWritePath(String writePath) {
        this.writePath = writePath;
        this.setProperty(EntryPointManager.KEY_WRITE_PATH, writePath);
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
        this.setProperty(EntryPointManager.KEY_API_PREFIX, apiPrefix);
    }

    public void setIsDebugger(String isDebugger) {
        this.isDebugger = isDebugger;
        this.setProperty(EntryPointManager.KEY_DEBUGGER, isDebugger);
    }

    public void setFormatException(String formatException) {
        this.formatException = formatException;
        this.setProperty(EntryPointManager.KEY_FORMAT_EXCEPTION, formatException);
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        this.setProperty(EntryPointManager.KEY_SCAN_PACKAGE, scanPackage);
    }

    public void setApiController(Set apiController) {
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

    public void setScanSpringContext(boolean scanSpringContext) {
        this.scanSpringContext = scanSpringContext;
        scanBeanToApiController();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        scanBeanToApiController();
    }

    private void scanBeanToApiController() {
        if (this.scanSpringContext) {
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(APIController.class);
            if (beans != null) {
                Set<Object> objects = new HashSet<Object>();
                for (Map.Entry<String, Object> entry : beans.entrySet()) {
                    objects.add(entry.getValue());
                }
                this.setClassesInstanceFromProperties(objects);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setScanClassAndInstance(servletContext);
    }

    @Override
    public void destroy() throws Exception {
        // TODO: 2016/5/19 Bean销毁时处理
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
