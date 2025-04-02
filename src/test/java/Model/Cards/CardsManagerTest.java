package Model.Cards;

import Model.Game.Board.Deck;
import Model.Game.Board.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class CardsManagerTest {

    @Test
    void createDecks() {
        assertThrows(IllegalStateException.class, () -> {
            CardsManager.createDecks(Level.LEARNING);
        });

        Deck[] decks = CardsManager.createDecks(Level.SECOND);
        assertNotNull(decks);
        assertEquals(4, decks.length);
        for (Deck deck : decks) {
            assertNotNull(deck);
            assertNotNull(deck.getCards());
            assertEquals(3, deck.getCards().size());
            assertEquals(1, deck.getCards().get(0).getCardLevel());
            assertEquals(2, deck.getCards().get(1).getCardLevel());
            assertEquals(2, deck.getCards().get(2).getCardLevel());
        }

        for (int i = 0; i < decks.length; i++) {
            for (int j = i + 1; j < decks.length; j++) {
                for (Card card : decks[i].getCards()) {
                    for (Card card2 : decks[j].getCards()) {
                        assertNotEquals(card, card2);
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4, 8, 12, 15, 17, 18})
    void createLearningDeck(int number) {
        Stack<Card> shuffled = CardsManager.createLearningDeck();

        assertNotNull(shuffled);
        for (Card card : shuffled) {
            assertNotNull(card);
        }
        assertTrue(shuffled.contains(CardsManager.getCard(number)));
    }

    @Test
    void createShuffledDeck() {
    }

    @Test
    void getCard() {
    }
}