package it.polimi.ingsw.event.game.clientToServer.player;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used to notify the server that the player has ended their turn.
 * It contains the userID of the player who ended their turn.
 *
 * @param userID The userID of the player who ended their turn.
 */
public record EndTurn(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the EndTurn event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the EndTurn event.
     */
    public static Responder<EndTurn> responder(EventTransceiver transceiver, Function<EndTurn, StatusEvent> response) {
        Responder<EndTurn> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the EndTurn event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the EndTurn event
     */
    public static Requester<EndTurn> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
