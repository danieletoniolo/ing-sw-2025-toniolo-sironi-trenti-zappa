package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player pick or leave a deck.
 * If the deck is in a hand, then the player will leave the deck from the hand
 * Otherwise, the player will pick the deck from the table
 *
 * @param nickname  The userID of the player who picked the deck.
 * @param usage     The usage of the deck: 0 = get, 1 = leave.
 * @param deckIndex The index of the deck in the hand or in the table.
 */
public record PickedLeftDeck(
        String nickname,
        int usage,
        int deckIndex
) implements Event, Serializable {
}
