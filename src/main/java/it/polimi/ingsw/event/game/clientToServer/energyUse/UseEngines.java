package it.polimi.ingsw.event.game.clientToServer.energyUse;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param userID       is the user ID. Only the user know his ID, so the event is not faked.
 * @param enginesIDs   The IDs of the engines to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 * @author Vittorio Sironi
 */
public record UseEngines(
        String userID,
        List<Integer> enginesIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the UseEngines event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the UseEngines event.
     */
    public static Responder<UseEngines> responder(EventTransceiver transceiver, Function<UseEngines, StatusEvent> response) {
        Responder<UseEngines> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the UseEngines event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the UseEngines event
     */
    public static Requester<UseEngines> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
