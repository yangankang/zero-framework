package com.yoosal.common;

public class Logger {
    public static Logger getLogger(Class clazz) {
        return new Logger(clazz);
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
