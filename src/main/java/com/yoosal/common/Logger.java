package com.yoosal.common;

import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.Properties;

public class Logger {
    public static Logger getLogger(Class clazz) {
        return new Logger(clazz);
    }

    static {
        Properties prop = new Properties();
        /**
         *  prop.setProperty("log4j.rootLogger", "DEBUG, CONSOLE");
         *  prop.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
         *  prop.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
         *  prop.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "%d{HH:mm:ss,SSS} [%t] %-5p %C{1} : %m%n");
         **/
        try {
            prop.load(Logger.class.getResourceAsStream("/log4j.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(prop);
    }

    private Class clazz;
    private org.apache.log4j.Logger logger;

    public Logger(Class clazz) {
        this.clazz = clazz;
        logger = org.apache.log4j.Logger.getLogger(clazz);
    }

    public void info(Object message) {
        logger.info(message);
    }

    public void warn(Object message) {
        logger.warn(message);
    }

    public void debug(Object message) {
        logger.debug(message);
    }

    public void debug(Object message, Throwable e) {
        logger.debug(message, e);
    }

    public void error(Object message) {
        logger.error(message);
    }

    public void error(Object message, Throwable e) {
        logger.error(message, e);
    }
}
