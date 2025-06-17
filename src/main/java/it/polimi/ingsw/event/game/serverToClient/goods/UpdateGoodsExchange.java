package it.polimi.ingsw.event.game.serverToClient.goods;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param nickname     is the user username when the event is sent to the other client.
 * @param exchangeData exchangeData contains an arraylist of pairs, each pairs contains (in this order) the storage ID and a list of goods IDs in the storage.
 * */
public record UpdateGoodsExchange (
        String nickname,
        List<Pair<Integer, List<Integer>>> exchangeData
) implements Event, Serializable {
}
