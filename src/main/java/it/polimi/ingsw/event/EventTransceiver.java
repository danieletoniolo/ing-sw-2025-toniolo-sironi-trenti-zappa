package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;

/**
 * Interface that combines event transmission and reception capabilities.
 * This interface extends both EventTransmitter and EventReceiver to provide
 * a unified interface for components that need to both send and receive events.
 * @author Daniele Toniolo
 */
public interface EventTransceiver extends EventTransmitter, EventReceiver<Event> {
}
