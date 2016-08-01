package com.yoosal.orm.core;

public class LocalSessionModel {
    private boolean isBegin = false;
    private SessionOperation sessionOperation = null;

    public LocalSessionModel(boolean isBegin, SessionOperation sessionOperation) {
        this.isBegin = isBegin;
        this.sessionOperation = sessionOperation;
    }

    public LocalSessionModel(SessionOperation sessionOperation) {
        this.sessionOperation = sessionOperation;
    }

    public boolean isBegin() {
        return isBegin;
    }

    public void setIsBegin(boolean isBegin) {
        this.isBegin = isBegin;
    }

    public SessionOperation getSessionOperation() {
        return sessionOperation;
    }

    public void setSessionOperation(SessionOperation sessionOperation) {
        this.sessionOperation = sessionOperation;
    }

    public void close(ThreadLocal<LocalSessionModel> threadLocal) {
        if (!isBegin) {
            sessionOperation.close();
            threadLocal.set(null);
        }
    }
}
