package com.yoosal.mvc.event;

import com.yoosal.common.event.Event;
import com.yoosal.common.event.EventOccurListener;

public abstract class RequestEventListener implements EventOccurListener {
    @Override
    public final void fireEvent(Event evt) {
        RequestEvent requestEvent = null;
        if (evt instanceof RequestEvent) {
            requestEvent = (RequestEvent) evt;
        }
        if (requestEvent != null) {
            if (requestEvent.isBefore()) {
                this.afterRequest(requestEvent);
            } else {
                this.beforeRequest(requestEvent);
            }
        }
    }

    public abstract void beforeRequest(RequestEvent evt);

    public abstract void afterRequest(RequestEvent evt);
}
