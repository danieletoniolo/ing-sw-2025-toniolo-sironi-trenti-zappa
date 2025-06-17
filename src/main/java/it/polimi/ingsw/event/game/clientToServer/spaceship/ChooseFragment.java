package it.polimi.ingsw.event.game.clientToServer.spaceship;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player have to choose the fragments of the ship. So it returns the fragments from which the user can choose
 * @param userID         is the user ID. Only the user know his ID, so the event is not faked.
 * @param fragmentChoice The fragment to keep
 */
public record ChooseFragment(
        String userID,
        int fragmentChoice
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the FragmentChoice event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the FragmentChoice event.
     */
    public static Responder<ChooseFragment> responder(EventTransceiver transceiver, Function<ChooseFragment, StatusEvent> response) {
        Responder<ChooseFragment> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the ChooseFragment event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the ChooseFragment event
     */
    public static Requester<ChooseFragment> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
