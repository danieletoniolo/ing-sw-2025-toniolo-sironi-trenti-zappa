package network;

import network.messages.Message;

public interface Connection {
    /**
     * Sends a message to the other end of the connection.
     * @param message the message to send
     */
    void send(Message message);

    /**
     * This method return the message received from the other end of the connection.
     * @return a Message object that has been received from the other end of the connection.
     */
    Message receive();

    void disconnect();
}
