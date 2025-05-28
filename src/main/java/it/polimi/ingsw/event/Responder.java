package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.CastEventReceiver;

import java.util.function.Function;

public class Responder<R extends Event, S extends Event> {
    public Responder(EventTransceiver transceiver, Function<R, S> response) {
        new CastEventReceiver<R>(transceiver).registerListener(event ->
                sendResponse(transceiver, event, response)
        );
    }

    private void sendResponse(EventTransceiver transceiver, R event, Function<R, S> responseFunc) {
        S response = responseFunc.apply(event);
        transceiver.broadcast(response);
    }
}
