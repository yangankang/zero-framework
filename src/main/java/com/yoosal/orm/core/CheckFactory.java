package com.yoosal.orm.core;

public class CheckFactory {

    public static Check getCheck(Class c) {
        try {
            return (Check) c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
