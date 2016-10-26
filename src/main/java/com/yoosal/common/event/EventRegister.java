package com.yoosal.common.event;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class EventRegister {
    private Map<Object, Vector<EventOccurListener>> listeners = new ConcurrentHashMap<Object, Vector<EventOccurListener>>();

    public synchronized void addListener(Object o, EventOccurListener evt) {
        Vector<EventOccurListener> eventOccurListeners = listeners.get(getObject(o));
        if (eventOccurListeners == null) {
            eventOccurListeners = new Vector<EventOccurListener>();
        }
        eventOccurListeners.addElement(evt);
        listeners.put(o, eventOccurListeners);
    }

    public synchronized void removeListener(Object o, EventOccurListener evt) {
        Vector<EventOccurListener> eventOccurListeners = listeners.get(getObject(o));
        if (eventOccurListeners == null) {
            eventOccurListeners = new Vector<EventOccurListener>();
        }
        eventOccurListeners.removeElement(evt);
        listeners.put(o, eventOccurListeners);
    }

    private Object getObject(Object o) {
        if (o instanceof String) {
            o = String.valueOf(o).intern();
        }
        return o;
    }

    @SuppressWarnings("unchecked")
    public void fireAEvent(Event evt) {
        Vector<EventOccurListener> currentListeners = listeners.get(evt.getType());
        synchronized (this) {
            currentListeners = (Vector<EventOccurListener>) currentListeners.clone();
        }
        for (int i = 0; i < currentListeners.size(); i++) {
            EventOccurListener listener = currentListeners
                    .elementAt(i);
            listener.fireEvent(evt);
        }
    }
}
