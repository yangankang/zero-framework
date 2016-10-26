package com.yoosal.common.event;

import java.util.EventListener;

public interface EventOccurListener extends EventListener {
    void fireEvent(Event evt);
}
