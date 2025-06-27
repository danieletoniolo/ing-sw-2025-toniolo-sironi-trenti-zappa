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
 * This event is used when a player has given up.
 * It is used to notify the other players that the player has given up.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @author Vittorio Sironi
 */
public record GiveUp(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the GiveUp event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the GiveUp event.
     */
    public static Responder<GiveUp> responder(EventTransceiver transceiver, Function<GiveUp, StatusEvent> response) {
        Responder<GiveUp> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the GiveUp event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the GiveUp event
     */
    public static Requester<GiveUp> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
