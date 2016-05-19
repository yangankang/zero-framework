package com.yoosal.mvc.exception;

public class InitializeSceneException extends RuntimeException {
    public InitializeSceneException(String msg) {
        super(msg);
    }

    public InitializeSceneException(String msg, Throwable e) {
        super(msg, e);
    }
}