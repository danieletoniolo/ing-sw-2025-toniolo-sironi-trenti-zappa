package it.polimi.ingsw.network;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.network.exceptions.DisconnectedConnection;

public interface Connection {
    /**
     * Sends a message to the other end of the connection.
     * @param message the event that represent the message to send
     */
    void send(Event message);

    /**
     * This method return the event which represent the message received from the other end of the connection.
     * @return an Event object that has been received from the other end of the connection.
     */
    Event receive() throws DisconnectedConnection, InterruptedException;

    /**
     * This method will be called when the connection is closed.
     */
    void disconnect();
}
