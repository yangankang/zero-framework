package com.yoosal.common.event;

import java.util.EventObject;

public abstract class Event extends EventObject {

    public Event(Object source) {
        super(source);
    }

    public abstract Object getType();
}
