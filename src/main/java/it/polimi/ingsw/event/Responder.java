package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.utils.Logger;

import java.util.UUID;
import java.util.function.Function;

public class Responder<R extends Event> {
    public Responder(EventTransceiver transceiver, Function<R, StatusEvent> response) {
        CastEventReceiver<R> receiver = new CastEventReceiver<>(transceiver);
        EventListener<R> eventListener = event -> sendResponse(transceiver, event, response);

        receiver.registerListener(eventListener);
    }

    private void sendResponse(EventTransceiver transceiver, R event, Function<R, StatusEvent> responseFunc) {
        StatusEvent response = responseFunc.apply(event);

        Logger.getInstance().log(Logger.LogLevel.INFO, "Sending response: " + response, false);
        transceiver.send(UUID.fromString(response.getUserID()), response);
    }
}
