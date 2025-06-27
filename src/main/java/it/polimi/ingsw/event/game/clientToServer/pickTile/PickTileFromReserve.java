package it.polimi.ingsw.event.game.clientToServer.pickTile;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player pick a tile from the reserve.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @param tileID The ID of the tile being picked.
 * @author Vittorio Sironi
 */
public record PickTileFromReserve(
        String userID,
        int tileID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PickTileFromReserve event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PickTileFromReserve event.
     */
    public static Responder<PickTileFromReserve> responder(EventTransceiver transceiver, Function<PickTileFromReserve, StatusEvent> response) {
        Responder<PickTileFromReserve> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the PickTileFromReserve event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PickTileFromReserve event
     */
    public static Requester<PickTileFromReserve> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
