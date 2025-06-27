package it.polimi.ingsw.event.game.serverToClient.deck;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This record represents an event where a shuffled deck of integers is sent to the client.
 * It contains a list of integers representing the cards in the deck.
 *
 * @param shuffledDeck A list of integers representing the shuffled deck.
 * @author Vittorio Sironi
 */
public record GetShuffledDeck(
        List<Integer> shuffledDeck
) implements Event, Serializable {
}
