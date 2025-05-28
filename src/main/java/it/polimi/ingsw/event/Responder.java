package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.game.EventWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;

import java.util.function.Function;

public class Responder<R extends Event, S extends Event> {
    public Responder(EventTransceiver transceiver, Function<R, S> response) {
        new CastEventReceiver<EventWrapper<R>>(transceiver).registerListener(eventWrapper ->
                sendResponse(transceiver, eventWrapper, response)
        );
    }

    private void sendResponse(EventTransceiver transceiver, EventWrapper<R> eventWrapper, Function<R, S> responseFunc) {
        S response = responseFunc.apply(eventWrapper.event());
        transceiver.broadcast(new EventWrapper<>(eventWrapper.ID(), response));
    }
}
