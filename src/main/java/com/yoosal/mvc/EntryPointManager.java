package com.yoosal.mvc;

import com.yoosal.common.ClassUtils;
import com.yoosal.common.CollectionUtils;
import com.yoosal.common.StringUtils;
import com.yoosal.common.event.DefaultEventContext;
import com.yoosal.common.event.PublicEventContext;
import com.yoosal.common.event.EventOccurListener;
import com.yoosal.common.scan.DefaultFrameworkScanClass;
import com.yoosal.common.scan.FrameworkScanClass;
import com.yoosal.mvc.annotation.APIController;
import com.yoosal.mvc.event.MVCEventType;
import com.yoosal.mvc.exception.MvcNotFoundConfigException;
import com.yoosal.mvc.exception.ParseTemplateException;
import com.yoosal.mvc.support.*;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * 这里是完成初始化工作,必须在启动的时候完成，读取配置，扫描类等
 */
public class EntryPointManager {

    /**
     * 所有的配置信息都在这里
     */
    private static final Map<String, Object> properties = new HashMap<String, Object>();

    //默认的类名的传参名称 比如：request.getParameter(DEFAULT_CLASS_KEY)
    private static final String DEFAULT_CLASS_KEY = "_class";
    //默认的方法名的传参名称 比如：request.getParameter(DEFAULT_METHOD_KEY)
    private static final String DEFAULT_METHOD_KEY = "_method";

    /**
     * classesInstanceFromProperties 和 classesInstanceFromScan 都是存放实例化的APIController对象，除了来源不一样其他
     * 的都一样的
     */
    private static Set<Object> classesInstanceFromProperties = null;
    private static Set<Object> classesInstanceFromScan = null;

    static final String KEY_WRITE_PATH = "mvc.write.path";
    static final String KEY_API_PREFIX = "mvc.api.prefix";
    static final String KEY_DEBUGGER = "mvc.debugger";
    static final String KEY_FORMAT_EXCEPTION = "mvc.format.exception";
    static final String KEY_SCAN_PACKAGE = "mvc.scan.package";
    static final String KEY_API_CLASS = "mvc.class.";
    //http请求中代表方法的字段
    static final String KEY_METHOD_KEY = "mvc.key.method";
    //http请求中代表类名的字段
    static final String KEY_CLASS_KEY = "mvc.key.class";
    static final String KEY_REQUEST_URI = "mvc.request.uri";
    static final String KEY_REQUEST_RESTFUL = "mvc.request.restful";
    static final String KEY_COMPRESSOR_JS = "mvc.compressor.js";
    static final String KEY_AUTH_CLASS = "mvc.auth.class";
    static final String KEY_API_CATCH_STRING = "mvc.api.catchFormat";
    static final String KEY_API_CATCH_CLASS = "mvc.api.catchClass";
    static final String KEY_EVENT_REQUEST_CLASS = "mvc.api.catchClass";


    private static final FrameworkScanClass frameworkScanClass = new DefaultFrameworkScanClass();
    private static final PublicEventContext eventContext = new DefaultEventContext();
    private static ViewResolver viewResolver = null;
    private static AuthoritySupport authoritySupport = null;

    /**
     * 将Properties文件中的配置转换成全局变量
     *
     * @param prop
     */
    public void setProperties(Properties prop, ServletContext servletContext) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (prop != null) {
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                properties.put((String) entry.getKey(), entry.getValue());
            }
        }
        classForName(prop);
        setScanClassAndInstance(servletContext);

        String authClass = (String) getProperty(KEY_AUTH_CLASS);
        if (authClass != null) {
            this.setAuthoritySupport((AuthoritySupport) Class.forName(authClass).newInstance());
        }
        this.setListenterFromProperties(prop);
    }

    public void setAuthoritySupport(AuthoritySupport authoritySupport) {
        EntryPointManager.authoritySupport = authoritySupport;
    }

    private void classForName(Properties prop) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Enumeration enumeration = prop.propertyNames();
        Set classes = new HashSet();
        while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            if (object != null) {
                String key = String.valueOf(object);
                if (key.startsWith(KEY_API_CLASS)) {
                    String className = prop.getProperty(key);
                    classes.add(Class.forName(className).newInstance());
                }
            }
        }
        classesInstanceFromProperties = classes;
    }

    private void setListenterFromProperties(Properties prop) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String cls = prop.getProperty(KEY_EVENT_REQUEST_CLASS);
        if (StringUtils.isNotBlank(cls)) {
            this.addRequestListener((EventOccurListener) Class.forName(cls).newInstance());
        }
    }

    public void setClassesInstanceFromProperties(Set set) {
        if (classesInstanceFromProperties != null) {
            classesInstanceFromProperties.addAll(set);
        } else {
            classesInstanceFromProperties = set;
        }
    }

    public void setClassesInstanceFromScan(Set set) {
        classesInstanceFromScan = set;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    private static Object getProperty(String key) {
        if (CollectionUtils.isEmpty(properties)) {
            throw new MvcNotFoundConfigException("not found config!");
        }
        return properties.get(key);
    }

    public static String getWritePath() {
        return (String) getProperty(KEY_WRITE_PATH);
    }

    public static String getApiPrefix() {
        return (String) getProperty(KEY_API_PREFIX);
    }

    public static boolean isDebugger() {
        String isDebuggerString = String.valueOf(getProperty(KEY_DEBUGGER));
        if (isDebuggerString.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static String getFormatException() {
        return (String) getProperty(KEY_FORMAT_EXCEPTION);
    }

    public static String getScanPackage() {
        return String.valueOf(getProperty(KEY_SCAN_PACKAGE));
    }

    public static Set getConfigApiClass() {
        return classesInstanceFromProperties;
    }

    public static String getMethodKey() {
        String ck = (String) getProperty(KEY_METHOD_KEY);
        if (StringUtils.isNotBlank(ck)) {
            return ck;
        } else {
            return DEFAULT_METHOD_KEY;
        }
    }

    public static String getClassKey() {
        String ck = (String) getProperty(KEY_CLASS_KEY);
        if (StringUtils.isNotBlank(ck)) {
            return ck;
        } else {
            return DEFAULT_CLASS_KEY;
        }
    }

    public static String getRequestUri() {
        return (String) getProperty(KEY_REQUEST_URI);
    }

    public static boolean isRestful() {
        String isRestful = String.valueOf(getProperty(KEY_REQUEST_RESTFUL));
        if (isRestful.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static String getCatchString() {
        return (String) getProperty(KEY_API_CATCH_STRING);
    }

    public static Class<? extends CatchFormat> getCatchClass() {
        String className = (String) getProperty(KEY_API_CATCH_CLASS);
        if (StringUtils.isNotBlank(className)) {
            try {
                return (Class<? extends CatchFormat>) ClassUtils.getDefaultClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ViewResolver getViewResolver() {
        if (viewResolver == null) {
            viewResolver = new NormalViewResolver();
            viewResolver.setEventContext(eventContext);
        }
        return viewResolver;
    }

    public static List getApiClass() {
        List classes = new ArrayList();
        if (classesInstanceFromProperties != null) {
            classes.addAll(classesInstanceFromProperties);
        }
        if (classesInstanceFromScan != null) {
            classes.addAll(classesInstanceFromScan);
        }
        return classes;
    }

    public static boolean isCompressorJs() {
        if (String.valueOf(getProperty(KEY_COMPRESSOR_JS)).equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static AuthoritySupport getAuthoritySupport() {
        return authoritySupport;
    }

    public void setScanClassAndInstance(ServletContext servletContext) throws InstantiationException, IllegalAccessException {
        //扫描并实例所有的类
        if (StringUtils.isNotBlank(getScanPackage())) {
            classesInstanceFromScan = frameworkScanClass.getScanClassAndInstance(getScanPackage(), APIController.class);
        }
        afterInstanceClassMethod(servletContext);
    }

    /**
     * 读取完成所有的配置之后，执行解析类，JS生成等功能
     */
    private void afterInstanceClassMethod(ServletContext servletContext) {
        SceneFactory.cacheControllerInfo();
        this.produceJavaScriptMapping(servletContext);
    }

    public void produceJavaScriptMapping(ServletContext servletContext) {
        JavaScriptMapping javaScriptMapping = new DefaultJavaScriptMapping();
        javaScriptMapping.setMethodParses(SceneFactory.getControllersInfo());
        if (StringUtils.isNotBlank(getWritePath())) {
            String webRootPath = servletContext.getRealPath(getWritePath());
            try {
                if (isCompressorJs()) {
                    javaScriptMapping.generateToFile(webRootPath, true);
                } else {
                    javaScriptMapping.generateToFile(webRootPath);
                }
            } catch (ParseTemplateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            javaScriptMapping.writeForDeveloper();
        }
    }

    public static void produceJavaScriptMapping(Writer out) throws IOException, ParseTemplateException {
        JavaScriptMapping javaScriptMapping = new DefaultJavaScriptMapping();
        javaScriptMapping.setMethodParses(SceneFactory.getControllersInfo());
        javaScriptMapping.setAuthoritySupport(authoritySupport);
        if (isCompressorJs()) {
            javaScriptMapping.generateToStream(out, true);
        } else {
            javaScriptMapping.generateToStream(out, false);
        }
    }

    public void addRequestListener(EventOccurListener listener) {
        eventContext.addListener(MVCEventType.REQUEST_EVERY_TIME, listener);
    }

    public static PublicEventContext getEventContext() {
        return eventContext;
    }
}
