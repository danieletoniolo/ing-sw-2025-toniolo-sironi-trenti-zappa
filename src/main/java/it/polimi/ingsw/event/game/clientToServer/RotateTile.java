package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This it.polimi.ingsw.event is sent when the user rotates a tile.
 * @param userID is the user ID. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param tileID is the ID of the tile that has been rotated.
 */
public record RotateTile(
        String userID,
        int tileID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the RotateTile it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the RotateTile it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<RotateTile, T> responder(EventTransceiver transceiver, Function<RotateTile, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the RotateTile it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the RotateTile it.polimi.ingsw.event
     */
    public static Requester<RotateTile> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
