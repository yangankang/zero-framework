package com.yoosal.orm;

import com.yoosal.orm.core.Operation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * 这里根据配置文件初始化整个框架，并且生成一个Operation
 */
public class OrmFactory {
    private static OperationManager operationManager = new OperationManager();

    public static void properties(String path) throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(path)));
        operationManager.setProperties(properties);
    }

    public Operation scene() {
        return null;
    }
}
