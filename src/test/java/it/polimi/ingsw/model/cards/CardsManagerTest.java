package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Deck;
import it.polimi.ingsw.model.game.board.Level;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class CardsManagerTest {

    @Test
    void createDecks() {
        CardsManager cardsManager = new CardsManager();
        assertNotNull(cardsManager);

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
        assertTrue(shuffled.getLast().getCardLevel() == 2 || shuffled.getLast().getCardLevel() == 1);
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