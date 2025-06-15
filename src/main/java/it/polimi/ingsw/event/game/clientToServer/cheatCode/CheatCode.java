package it.polimi.ingsw.event.game.clientToServer.cheatCode;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

public record CheatCode(
        String userID,
        int shipIndex
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the CheatCode event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the CheatCode event.
     */
    public static Responder<CheatCode> responder(EventTransceiver transceiver, Function<CheatCode, StatusEvent> response) {
        Responder<CheatCode> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the CheatCode event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the CheatCode event
     */
    public static Requester<CheatCode> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
