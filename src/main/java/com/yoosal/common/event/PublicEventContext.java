package com.yoosal.common.event;

public abstract class PublicEventContext {

    private EventRegister eventRegister = new EventRegister();

    public void addListener(Object type, EventOccurListener listener) {
        eventRegister.addListener(type, listener);
    }

    public void removeListener(Object type, EventOccurListener listener) {
        eventRegister.removeListener(type, listener);
    }

    public void fireCEvent(Event evt) {
        eventRegister.fireAEvent(evt);
    }
}
