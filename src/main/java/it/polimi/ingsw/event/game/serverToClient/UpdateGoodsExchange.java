package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param nickname     is the user username when the event is sent to the other client.
 * @param exchangeData exchangeData contains an arraylist of triplets, each triplet contains (in this order) the goods that the player wants to get, the good that the player wants to leave and the storage ID
 * */
public record UpdateGoodsExchange (
        String nickname,
        List<Triplet<List<Integer>, List<Integer>, Integer>> exchangeData
) implements Event, Serializable {
}
