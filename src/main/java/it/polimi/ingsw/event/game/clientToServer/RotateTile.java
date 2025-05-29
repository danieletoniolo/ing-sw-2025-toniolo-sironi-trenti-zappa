package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is sent when the user rotates a tile.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @param tileID is the ID of the tile that has been rotated.
 */
public record RotateTile(
        String userID,
        int tileID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the RotateTile event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the RotateTile event.
     */
    public static Responder<RotateTile> responder(EventTransceiver transceiver, Function<RotateTile, StatusEvent> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the RotateTile event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the RotateTile event
     */
    public static Requester<RotateTile> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
