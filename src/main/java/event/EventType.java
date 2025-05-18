package event;

import java.io.Serializable;

public class EventType<T extends Event> implements Serializable {
    private final Class<T> eventClass;

    public EventType(Class<T> eventClass) {
        this.eventClass = eventClass;
    }

    public Class<T> getEventClass() {
        return eventClass;
    }
}
