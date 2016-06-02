package com.yoosal.orm.exception;

public class SQLDialectException extends RuntimeException {
    public SQLDialectException(String message) {
        super(message);
    }

    public SQLDialectException(String message, Throwable cause) {
        super(message, cause);
    }
}
