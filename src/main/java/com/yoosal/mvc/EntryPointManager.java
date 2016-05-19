package com.yoosal.mvc;

import com.yoosal.common.CollectionUtils;
import com.yoosal.common.scan.DefaultFrameworkScanClass;
import com.yoosal.common.scan.FrameworkScanClass;
import com.yoosal.mvc.annotation.ApiController;
import com.yoosal.mvc.exception.MvcNotFoundConfigException;

import java.util.*;

/**
 * 这里是完成初始化工作,必须在启动的时候完成，读取配置，扫描类等
 */
public final class EntryPointManager {

    private static final Map<String, Object> properties = new HashMap<String, Object>();
    private static List classesInstanceFromProperties = null;
    private static List classesInstanceFromScan = null;
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


    private static FrameworkScanClass frameworkScanClass = new DefaultFrameworkScanClass();

    /**
     * 将Properties文件中的配置转换成全局变量
     *
     * @param prop
     */
    public static void setProperties(Properties prop) {

    }

    private static void classForName(Properties prop) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Enumeration enumeration = prop.propertyNames();
        List classes = new ArrayList();
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

    public static void setClassesInstanceFromProperties(List list) {
        classesInstanceFromProperties = list;
    }

    public static void setClassesInstanceFromScan(List list) {
        classesInstanceFromScan = list;
    }

    public static void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    private static Object getProperty(String key) {
        if (CollectionUtils.isEmpty(properties)) {
            throw new MvcNotFoundConfigException("not found config!");
        }
        return properties.get(key);
    }

    public static String getWritePath() {
        return String.valueOf(getProperty(KEY_WRITE_PATH));
    }

    public static String getApiPrefix() {
        return String.valueOf(getProperty(KEY_API_PREFIX));
    }

    public static boolean isDebugger() {
        String isDebuggerString = String.valueOf(getProperty(KEY_DEBUGGER));
        if (isDebuggerString.equalsIgnoreCase("true")) {
            return true;
        }
        return false;
    }

    public static String getFormatException() {
        return String.valueOf(getProperty(KEY_FORMAT_EXCEPTION));
    }

    public static String getScanPackage() {
        return String.valueOf(getProperty(KEY_SCAN_PACKAGE));
    }

    public static List getConfigApiClass() {
        return classesInstanceFromProperties;
    }

    public static String getMethodKey() {
        return String.valueOf(getProperty(KEY_METHOD_KEY));
    }

    public static String getClassKey() {
        return String.valueOf(getProperty(KEY_CLASS_KEY));
    }

    public static String getRequestUri() {
        return String.valueOf(getProperty(KEY_REQUEST_URI));
    }

    public static List getApiClass() {
        List classes = new ArrayList();
        classes.addAll(classesInstanceFromProperties);
        classes.addAll(classesInstanceFromScan);
        return classes;
    }

    public static void setScanClassAndInstance() throws InstantiationException, IllegalAccessException {
        //扫描并实例所有的类
        classesInstanceFromScan = frameworkScanClass.getScanClassAndInstance(getScanPackage(), ApiController.class);
    }
}
