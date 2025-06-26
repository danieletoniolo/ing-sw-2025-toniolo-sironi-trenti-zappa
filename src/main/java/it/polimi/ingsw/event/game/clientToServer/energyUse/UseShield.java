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
 * This event is used when a player uses a shield.
 * @param userID    is the user ID. Only the user know his ID, so the event is not faked.
 * @param batteryID is the ID of the battery used to use the shield.
 *                  It is a list for an easier implementation in the GUI, but the size should be 1
 */
public record UseShield(
        String userID,
        List<Integer> batteryID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the UseShield event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the UseShield event.
     */
    public static Responder<UseShield> responder(EventTransceiver transceiver, Function<UseShield, StatusEvent> response) {
        Responder<UseShield> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the UseShield event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the UseShield event
     */
    public static Requester<UseShield> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
