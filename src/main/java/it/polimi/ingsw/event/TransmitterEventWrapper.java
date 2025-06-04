package it.polimi.ingsw.event;

import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.event.type.StatusEvent;

/**
 * Encapsulates an {@link EventTransmitter} and a {@link StatusEvent} into a single immutable data structure.
 * This record is used as a wrapper to associate an event transmission mechanism with a specific status event.
 *
 * The {@link EventTransmitter} is responsible for broadcasting or sending event data to connected entities,
 * while the {@link StatusEvent} represents specific event-related data, particularly with a user ID and status information.
 *
 * This class is utilized in the context of facilitating event handling and response mechanisms, bridging event
 * transmitters with the corresponding status events.
 */
public record TransmitterEventWrapper(EventTransmitter transmitter, StatusEvent event) {
}
