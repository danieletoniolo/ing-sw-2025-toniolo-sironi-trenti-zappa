package controller.event;

public interface EventTransceiver {
    /**
     * Broadcasts the given {@link Event  } to all the {@link network.Connection} present in the transceiver.
     * @param data is the {@link Event} which will be broadcast.
     */
    void broadcast(Event data);
}
