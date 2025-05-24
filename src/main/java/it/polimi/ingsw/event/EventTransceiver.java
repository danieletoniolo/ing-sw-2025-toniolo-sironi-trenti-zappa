package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;

public interface EventTransceiver extends EventTransmitter, EventReceiver<Event> {
}
