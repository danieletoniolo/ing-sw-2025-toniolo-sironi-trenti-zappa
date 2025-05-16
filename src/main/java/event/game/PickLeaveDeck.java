package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player pick or leave a deck.
 * If the deck is in a hand, then the player will leave the deck from the hand
 * Otherwise, the player will pick the deck from the table
 *
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param deckIndex The index of the deck in the hand or in the table.
 */
public record PickLeaveDeck(
        String userID,
        int deckIndex
) implements Event, Serializable {
}
