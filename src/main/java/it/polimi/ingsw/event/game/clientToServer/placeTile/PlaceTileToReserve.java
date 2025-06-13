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
 * This event is used when a player place a tile on the reserve.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 */
public record PlaceTileToReserve(
    String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PlaceTileToReserve event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PlaceTileToReserve event.
     */
    public static Responder<PlaceTileToReserve> responder(EventTransceiver transceiver, Function<PlaceTileToReserve, StatusEvent> response) {
        Responder<PlaceTileToReserve> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the PlaceTileToReserve event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PlaceTileToReserve event
     */
    public static Requester<PlaceTileToReserve> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
