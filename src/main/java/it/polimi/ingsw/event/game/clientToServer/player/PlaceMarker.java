package it.polimi.ingsw.event.game.clientToServer.player;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player have to move the marker on the board.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @param position The new position of the marker on the board. It is not the number of the position to add to the current position
 */
public record PlaceMarker(
        String userID,
        int position
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PlaceMarker event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PlaceMarker event.
     */
    public static Responder<PlaceMarker> responder(EventTransceiver transceiver, Function<PlaceMarker, StatusEvent> response) {
        Responder<PlaceMarker> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the PlaceMarker event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PlaceMarker event
     */
    public static Requester<PlaceMarker> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
