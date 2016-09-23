package com.yoosal.orm.core;

public class LocalSessionModel {
    private boolean isBegin = false;
    private SessionOperation sessionOperation = null;
    private int count = 0;

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
        if (isBegin) {
            count++;
        } else {
            count--;
            if (count < 0) {
                count = 0;
            }
        }
        this.isBegin = isBegin;
    }

    public SessionOperation getSessionOperation() {
        return sessionOperation;
    }

    public void setSessionOperation(SessionOperation sessionOperation) {
        this.sessionOperation = sessionOperation;
    }

    public void close(ThreadLocal<LocalSessionModel> threadLocal) {
        if (!isBegin && count <= 0) {
            sessionOperation.close();
            threadLocal.set(null);
        }
    }

    public boolean canCommit() {
        if (!isBegin && count <= 0) {
            return true;
        } else {
            return false;
        }
    }
}
