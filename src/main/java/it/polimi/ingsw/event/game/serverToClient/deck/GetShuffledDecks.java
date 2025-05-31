package it.polimi.ingsw.event.game.serverToClient.deck;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;
import java.util.Stack;

/**
 * This record represents an event where a shuffled deck of integers is sent to the client.
 * It contains a list of integers representing the cards in the deck.
 *
 * @param shuffledDeck A list of integers representing the shuffled deck.
 */
public record GetShuffledDecks(
        List<Integer> shuffledDeck
) implements Event, Serializable {
}
