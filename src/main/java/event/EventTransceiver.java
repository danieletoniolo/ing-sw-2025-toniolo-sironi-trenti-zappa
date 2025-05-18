package event;

import network.Connection;

public interface EventTransceiver {
    /**
     * Broadcasts the given {@link Event  } to all the {@link network.Connection} present in the transceiver.
     * @param data is the {@link Event} which will be broadcast.
     */
    void broadcast(Event data);

    /**
     * Sends the given {@link Event} to the specified {@link network.Connection}.
     * @param connection is the {@link network.Connection} to which the event will be sent.
     * @param data is the {@link Event} to send.
     */
    void send(Connection connection, Event data);
}
