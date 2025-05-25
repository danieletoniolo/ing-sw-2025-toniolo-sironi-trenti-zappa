package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when a player have to exchange goods between two storages.
 * @param userID       is the user ID. Only the user know his ID, so the event is not faked.
 * @param exchangeData exchangeData contains an arraylist of triplets, each triplet contains (in this order) the goods that the player wants to get, the good that the player wants to leave and the storage ID
 * */
public record ExchangeGoods(
        String userID,
        List<Triplet<List<Integer>, List<Integer>, Integer>> exchangeData
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the ExchangeGoods event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the ExchangeGoods event.
     */
    public static <T extends Event> Responder<ExchangeGoods, T> responder(EventTransceiver transceiver, Function<ExchangeGoods, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the ExchangeGoods event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the ExchangeGoods event
     */
    public static Requester<ExchangeGoods> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
