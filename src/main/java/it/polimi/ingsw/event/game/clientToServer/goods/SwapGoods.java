package it.polimi.ingsw.event.game.clientToServer.goods;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * This class represents the swap of goods between two storages.
 * @param userID     is the user ID. Only the user know his ID, so the event is not faked.
 * @param storageID1 is the ID of the first storage.
 * @param storageID2 is the ID of the second storage.
 * @param goods1to2  is the list of goods to swap from storage 1 to storage 2.
 * @param goods2to1  is the list of goods to swap from storage 2 to storage 1.
 * @author Vittorio Sironi
 */
public record SwapGoods(
        String userID,
        int storageID1,
        int storageID2,
        List<Integer> goods1to2,
        List<Integer> goods2to1
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the SwapGoods event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the SwapGoods event.
     */
    public static Responder<SwapGoods> responder(EventTransceiver transceiver, Function<SwapGoods, StatusEvent> response) {
        Responder<SwapGoods> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the SwapGoods event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SwapGoods event
     */
    public static Requester<SwapGoods> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
