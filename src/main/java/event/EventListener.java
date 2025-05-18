package event;

public interface EventListener<T extends Event> {
    /**
     * It will handle the event that the server receive from the client
     * It is a callback that will be invoked by the {@link NetworkTransceiver}
     * @param event the event to handle
     */
    void handle(T event);
}
