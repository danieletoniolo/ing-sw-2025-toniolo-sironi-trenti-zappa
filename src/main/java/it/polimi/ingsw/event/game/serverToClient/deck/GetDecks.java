package it.polimi.ingsw.event.game.serverToClient.deck;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to send the decks of the game to the client.
 * It contains a list of lists of integers, where each inner list represents a deck.
 *
 * @param decks A list of lists of integers representing the cards in the decks.
 * @author Vittorio Sironi
 */
public record GetDecks(
        List<List<Integer>> decks
) implements Event, Serializable {
}
