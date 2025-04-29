package Model.Cards;

import Model.Game.Board.Board;
import Model.Game.Board.Deck;
import Model.Game.Board.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
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
        Deck[] decks = CardsManager.createDecks(Level.SECOND);
        Stack<Card> shuffled = CardsManager.createShuffledDeck(decks);

        assertEquals(decks.length * 3, shuffled.size());
        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                assertTrue(shuffled.contains(card));
            }
        }
        assertEquals(2, shuffled.getLast().getCardLevel());
    }

    @Test
    void getCard() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            CardsManager.getCard(40);
        });
        for (int i = 0; i < 40; i++) {
            assertEquals(i, CardsManager.getCard(i).getID());
            if (i < 20) assertEquals(1, CardsManager.getCard(i).getCardLevel());
            else assertEquals(2, CardsManager.getCard(i).getCardLevel());
        }
    }

    @Test
    void multiGame() throws JsonProcessingException {
        ArrayList<Board> boards = new ArrayList<>();
        boards.add(new Board(Level.SECOND));
        boards.add(new Board(Level.SECOND));
        boards.add(new Board(Level.SECOND));
        boards.add(new Board(Level.SECOND));


        for (Board board : boards) {
            for(Board board2 : boards) {
                if (!board.equals(board2)) {
                    for(Card card : board.getShuffledDeck()) {
                        for (Card card2 : board2.getShuffledDeck()) {
                            if (card.getID() == card2.getID()) {
                                assertEquals(card, card2);
                            }
                        }
                    }
                }
            }
        }
    }
}