package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This class represents the event of a player being isReady in the game.
 * @param userID  The userID of the player
 * @param isReady The isReady status of the player
 * @author Vittorio Sironi
 */
public record PlayerReady(
        String userID,
        boolean isReady
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PlayerReady event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PlayerReady event.
     */
    public static Responder<PlayerReady> responder(EventTransceiver transceiver, Function<PlayerReady, StatusEvent> response) {
        Responder<PlayerReady> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the PlayerReady event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return            a Requester for the PlayerReady event
     */
    public static Requester<PlayerReady> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
