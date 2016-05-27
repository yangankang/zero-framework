package com.yoosal.orm;

import com.yoosal.common.CollectionUtils;
import com.yoosal.common.Logger;
import com.yoosal.mvc.exception.MvcNotFoundConfigException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ORM 框架的所有配置Bean，和mvc的很相似
 */
public class OperationManager {
    private static final Logger logger = Logger.getLogger(OperationManager.class);
    /**
     * 全局配置缓存的Map
     */
    private static final Map<String, Object> properties = new HashMap<String, Object>();

    static final String KEY_SCAN_PACKAGE = "orm.scan.package";

    public void setProperties(Properties prop) {
        if (prop != null) {
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                properties.put((String) entry.getKey(), entry.getValue());
            }
        }
    }

    private static Object getProperty(String key) {
        if (CollectionUtils.isEmpty(properties)) {
            logger.error("not found orm config");
        }
        return properties.get(key);
    }

    public static String getScanPackage() {
        return (String) getProperty(KEY_SCAN_PACKAGE);
    }
}
