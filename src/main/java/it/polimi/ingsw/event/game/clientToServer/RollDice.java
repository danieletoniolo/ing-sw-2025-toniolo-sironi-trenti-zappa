package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player rolls the dice.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 */
public record RollDice(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the RollDice event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the RollDice event.
     */
    public static <T extends Event> Responder<RollDice, T> responder(EventTransceiver transceiver, Function<RollDice, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the RollDice event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the RollDice event
     */
    public static Requester<RollDice> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
