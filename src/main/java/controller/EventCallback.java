package controller;

import event.Event;

/**
 * It is used by the states and the gameController to notify to the matchController that an event need to be sent in broadcast
 */
public interface EventCallback {
    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController that an event need to be sent in broadcast
     * @param event is the event to be sent in broadcast
     */
    public void trigger(Event event);
}
