package event.game.serverToClient;

import Model.Good.Good;
import event.eventType.Event;
import event.EventTransceiver;
import event.Responder;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param nickname     is the user username when the event is sent to the other client.
 * @param exchangeData exchangeData contains an arraylist of triplets, each triplet contains (in this order) the goods that the player wants to get, the good that the player wants to leave and the storage ID
 * */
public record UpdateGoodsExchange (
        String nickname,
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData
) implements Event, Serializable {
}
