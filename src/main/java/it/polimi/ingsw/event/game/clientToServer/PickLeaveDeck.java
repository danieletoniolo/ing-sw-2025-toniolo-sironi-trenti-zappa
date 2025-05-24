package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player pick or leave a deck.
 * If the deck is in a hand, then the player will leave the deck from the hand
 * Otherwise, the player will pick the deck from the table
 *
 * @param userID    is the user ID. Only the user know his ID, so the event is not faked.
 * @param usage     The usage of the deck: 0 = get, 1 = leave.
 * @param deckIndex The index of the deck in the hand or in the table.
 */
public record PickLeaveDeck(
        String userID,
        int usage,
        int deckIndex
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the PickLeaveDeck event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the PickLeaveDeck event.
     */
    public static <T extends Event> Responder<PickLeaveDeck, T> responder(EventTransceiver transceiver, Function<PickLeaveDeck, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the PickLeaveDeck event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the PickLeaveDeck event
     */
    public static Requester<PickLeaveDeck> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
