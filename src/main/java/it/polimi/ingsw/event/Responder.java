package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

public class Responder<R extends Event, S extends Event> {
    public Responder(EventTransceiver transceiver, Function<R, S> response) {
        CastEventReceiver<EventWrapper<R>> transceiverReceiver = new CastEventReceiver<>(transceiver);
        EventListener<EventWrapper<R>> eventListener = eventWrapper -> {
            sendResponse(transceiver, eventWrapper.getEvent(), response);
        };

        transceiverReceiver.registerListener(eventListener);
    }

    private void sendResponse(EventTransceiver transceiver, R event, Function<R, S> responseFunc) {
        S response = responseFunc.apply(event);

        Logger.getInstance().log(Logger.LogLevel.INFO, "Sending response: " + response, false);
        transceiver.broadcast(response);
    }
}
