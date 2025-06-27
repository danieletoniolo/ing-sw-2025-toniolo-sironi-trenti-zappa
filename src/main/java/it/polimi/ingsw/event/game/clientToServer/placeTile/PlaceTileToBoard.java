package it.polimi.ingsw.event.game.clientToServer.placeTile;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player place a tile on the board.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @author Vittorio Sironi
 */
public record PlaceTileToBoard(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PlaceTileToBoard event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PlaceTileToBoard event.
     */
    public static Responder<PlaceTileToBoard> responder(EventTransceiver transceiver, Function<PlaceTileToBoard, StatusEvent> response) {
        Responder<PlaceTileToBoard> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the PlaceTileToBoard event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PlaceTileToBoard event
     */
    public static Requester<PlaceTileToBoard> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
