package com.yoosal.mvc.exception;

public class ParseTemplateException extends Exception {
    public ParseTemplateException(String msg) {
        super(msg);
    }

    public ParseTemplateException(String msg, Exception e) {
        super(msg, e);
    }
}