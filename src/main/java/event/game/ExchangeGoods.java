package event.game;

import Model.Good.Good;
import event.Event;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param exchangeData exchangeData contains an arraylist of triplets, each triplet contains (in this order) the goods that the player wants to get, the good that the player wants to leave and the storage ID
 * */
public record ExchangeGoods(
        String userID,
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData
) implements Event, Serializable {
}
