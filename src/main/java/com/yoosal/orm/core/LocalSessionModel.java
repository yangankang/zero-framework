package com.yoosal.orm.core;

public class LocalSessionModel {
    private SessionOperation sessionOperation = null;

    public LocalSessionModel(SessionOperation sessionOperation) {
        this.sessionOperation = sessionOperation;
    }

    public SessionOperation getSessionOperation() {
        return sessionOperation;
    }

    public void setSessionOperation(SessionOperation sessionOperation) {
        this.sessionOperation = sessionOperation;
    }

    public void close(ThreadLocal<LocalSessionModel> threadLocal) {
        if (!sessionOperation.isTransacting()) {
            sessionOperation.close();
            threadLocal.set(null);
        }
    }
}
