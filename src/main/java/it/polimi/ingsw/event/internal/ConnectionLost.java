package it.polimi.ingsw.event.internal;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.UUID;

public record ConnectionLost(
        UUID userID
) implements Event, Serializable {
    public static void registerHandler(EventTransceiver transceiver, EventListener<ConnectionLost> listener) {
        new CastEventReceiver<ConnectionLost>(transceiver).registerListener(listener);
    }
}
