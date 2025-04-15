package Model.Game.Board;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @ParameterizedTest
    @CsvSource({
            "LEARNING, 18",
            "SECOND, 24"

    })
    void getStepsForALap(Level level, int expected) throws JsonProcessingException {
        Board board = new Board(level);
        assertEquals(expected, board.getStepsForALap());
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void getBoardLevel(Level level) throws JsonProcessingException {
        assertEquals(level, new Board(level).getBoardLevel());
    }

    @Test
    void getDeck() {
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void getTile(Level level) throws JsonProcessingException {
        /*Board board = new Board(level);
        for (int i = -1; i < 157; i++) {
            final int index = i;
            if (i < 0) {
                assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(index));
            }
            if (i > 0 && i < 156) {
                assertEquals(i, board.getTile(i).getID());
            }
            if (i > 155) {
                assertThrows(IndexOutOfBoundsException.class, () -> board.getTile(index));
            }
        }

         */
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void drawCard(Level level) throws JsonProcessingException {
        Board board = new Board(level);

        switch (level) {
            case LEARNING -> assertEquals(1, board.drawCard().getCardLevel());
            case SECOND -> assertEquals(2, board.drawCard().getCardLevel());
        }
        assertEquals(board.getShuffledDeck().peek(), board.drawCard());
        int before = board.getShuffledDeck().size();
        board.drawCard();
        assertEquals(before - 1, board.getShuffledDeck().size());

        while (!board.getShuffledDeck().isEmpty()) {
            board.drawCard();
        }

        assertThrows(IllegalStateException.class, board::drawCard, "No more cards in the deck");
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void getShuffledDeck(Level level) throws JsonProcessingException {
        Board board = new Board(level);

        assertNotNull(board.getShuffledDeck());
        assertFalse(board.getShuffledDeck().isEmpty());
        while (!board.getShuffledDeck().isEmpty()) {
            board.drawCard();
        }
        assertTrue(board.getShuffledDeck().isEmpty());
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void setPlayer(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        PlayerData[] players = new PlayerData[4];
        players[0] = new PlayerData("world", null, null);
        players[1] = new PlayerData("you", null, null);
        players[3] = new PlayerData("are", null, null);

        assertThrows(NullPointerException.class, () -> {
            board.setPlayer(players[2], 0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], -1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], 4);
        });

        players[2] = new PlayerData("hello", null, null);

        board.setPlayer(players[0], 0);
        board.setPlayer(players[1], 1);
        board.setPlayer(players[2], 2);
        board.setPlayer(players[3], 3);
        switch (level) {
            case LEARNING:
                assertEquals(4, players[0].getStep());
                assertEquals(2, players[1].getStep());
                assertEquals(1, players[2].getStep());
                assertEquals(0, players[3].getStep());
                break;
            case SECOND:
                assertEquals(6, players[0].getStep());
                assertEquals(3, players[1].getStep());
                assertEquals(1, players[2].getStep());
                assertEquals(0, players[3].getStep());
                break;
        }
    }

    @Test
    void updateInGamePlayers() {
    }

    @Test
    void addSteps() {
    }

    @Test
    void getGaveUpPlayers() {
    }
}