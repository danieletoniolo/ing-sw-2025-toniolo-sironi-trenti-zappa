package it.polimi.ingsw.event.trasmitter;

import it.polimi.ingsw.event.type.Event;

import java.util.UUID;

/**
 * Represents a mechanism for transmitting events to multiple connections. An implementation of this
 * interface should provide the ability to broadcast events to all connected entities, as well as
 * sending events to specific connections.
 */
public interface EventTransmitter {
    /**
     * Broadcasts the specified it.polimi.ingsw.event to all connected entities. This method is typically used
     * to transmit an it.polimi.ingsw.event to all relevant listeners or recipients simultaneously.
     *
     * @param data the {@link Event} to be broadcast to all connections
     */
    void broadcast(Event data);

    /**
     * Sends the specified it.polimi.ingsw.event to a given connection. This method is used to transmit
     * data or messages to a specific recipient identified by the provided connection.
     *
     * @param uuid the {@link UUID} of the connection to which the it.polimi.ingsw.event should be sent
     * @param data the {@link Event} object containing the data or message to be transmitted
     */
    void send(UUID uuid, Event data);
}
