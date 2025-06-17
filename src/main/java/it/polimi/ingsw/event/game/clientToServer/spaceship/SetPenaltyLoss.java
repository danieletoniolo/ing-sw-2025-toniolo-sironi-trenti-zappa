package it.polimi.ingsw.event.game.clientToServer.spaceship;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * This record represents the penalty loss.
 * @param userID      is the user ID. Only the user know his ID, so the event is not faked.
 * @param type        Type of the penalty: 0 = goods, 1 = batteries, 2 = crew members.
 * @param penaltyLoss List of Integers representing the ID of storage, batteries or cabins from
 *                    which we take the penalty to serve.
 *                    We pick one from each ID in the list, in case of goods we pick the most valuable ones.
 */
public record SetPenaltyLoss(
        String userID,
        int type,
        List<Integer> penaltyLoss
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the SetPenaltyLoss event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the SetPenaltyLoss event.
     */
    public static Responder<SetPenaltyLoss> responder(EventTransceiver transceiver, Function<SetPenaltyLoss, StatusEvent> response) {
        Responder<SetPenaltyLoss> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the SetPenaltyLoss event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SetPenaltyLoss event
     */
    public static Requester<SetPenaltyLoss> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
