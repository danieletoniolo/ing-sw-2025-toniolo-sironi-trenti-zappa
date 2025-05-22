package network;

import event.eventType.Event;

public interface Connection {
    /**
     * Sends a message to the other end of the connection.
     * @param message the event that represent the message to send
     */
    void send(Event message);

    /**
     * This method return the event which represent the message received from the other end of the connection.
     * @return a Event object that has been received from the other end of the connection.
     */
    Event receive();

    /**
     * This method will be called when the connection is closed.
     */
    void disconnect();
}
