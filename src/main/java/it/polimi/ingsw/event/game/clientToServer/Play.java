package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This class represents the event of a player starting to play the game.
 * @param userID The userID of the player who is starting to play.
 */
public record Play(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the Play event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the Play event.
     */
    public static Responder<Play> responder(EventTransceiver transceiver, Function<Play, StatusEvent> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the Play event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the Play event
     */
    public static Requester<Play> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
