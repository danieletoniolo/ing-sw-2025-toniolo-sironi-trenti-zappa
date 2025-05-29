package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;

public class EventWrapper<T extends Event> implements Event {
    private final T event;

    public EventWrapper(T event) {
        this.event = event;
    }

    public T getEvent() {
        return event;
    }
}
