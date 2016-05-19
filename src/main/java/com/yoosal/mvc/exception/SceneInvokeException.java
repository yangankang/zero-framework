package com.yoosal.mvc.exception;

public class SceneInvokeException extends Exception {
    public SceneInvokeException(String msg) {
        super(msg);
    }

    public SceneInvokeException(String msg, Exception e) {
        super(msg, e);
    }
}