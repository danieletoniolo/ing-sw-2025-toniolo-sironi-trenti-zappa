package it.polimi.ingsw.event.game.clientToServer.pickTile;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player pick a tile from the board.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @param tileID The ID of the tile being picked.
 */
public record PickTileFromBoard(
        String userID,
        int tileID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PickTileFromBoard event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PickTileFromBoard event.
     */
    public static Responder<PickTileFromBoard> responder(EventTransceiver transceiver, Function<PickTileFromBoard, StatusEvent> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the PickTileFromBoard event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PickTileFromBoard event
     */
    public static Requester<PickTileFromBoard> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
