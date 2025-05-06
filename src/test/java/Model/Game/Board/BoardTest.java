package Model.Game.Board;

import Model.Cards.Card;
import Model.Cards.CardsManager;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Component;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        assertThrows(IllegalArgumentException.class, () -> {
            new Board(null);
        });
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
    void getTiles(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        assertNotNull(board.getTiles());
        assertEquals(152, board.getTiles().length);
        for (int i = 0; i < board.getTiles().length; i++) {
            assertNotNull(board.getTiles()[i]);
            assertEquals(i, board.getTiles()[i].getID());
        }
    }

    @Test
    void getDeck() throws JsonProcessingException {
        assertThrows(IllegalStateException.class, () -> {
            Board board = new Board(Level.LEARNING);
            board.getDeck(0, new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        Board board = new Board(Level.SECOND);
        assertThrows(NullPointerException.class, () -> {
            board.getDeck(0, null);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getDeck(-1, new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null));
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getDeck(4, new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        for (int i = 0; i < 3; i++) {
            int index = i;
            assertThrows(IllegalStateException.class, () -> {
                SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.RED);
                PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, spaceShip);
                board.getDeck(index, player);
            });
        }

        assertThrows(IllegalStateException.class, () -> {
            board.getDeck(3, new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        for (int i = 0; i < 3; i++) {
            SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.RED);
            PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, spaceShip);
            spaceShip.placeComponent(board.getTiles()[7], 6, 7);
            assertNotNull(board.getDeck(i, player));
        }
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void setPlayer(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        PlayerData[] players = new PlayerData[4];
        players[0] = new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null);
        players[1] = new PlayerData("123e4567-e89b-12d3-a456-426614174002", null, null);
        players[3] = new PlayerData("123e4567-e89b-12d3-a456-426614174004", null, null);

        assertThrows(NullPointerException.class, () -> {
            board.setPlayer(players[2], 0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], -1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], 4);
        });

        players[2] = new PlayerData("123e4567-e89b-12d3-a456-426614174003", null, null);

        board.clearInGamePlayers();
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

        assertThrows(IllegalStateException.class, () -> {
            board.setPlayer(players[0], 0);
        });
        assertThrows(IllegalStateException.class, () -> {
            board.setPlayer(players[1], 0);
        });
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void updateInGamePlayers(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        board.clearInGamePlayers();
        ArrayList<PlayerData> players = new ArrayList<>(3);

        PlayerData player1 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null);
        PlayerData player2 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", null, null);
        PlayerData player3 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", null, null);

        players.add(0, player1);
        players.add(1, player2);
        players.add(2, player3);

        board.setPlayer(player1, 0);
        board.setPlayer(player2, 1);
        board.setPlayer(player3, 3);
        board.refreshInGamePlayers();
        assertEquals(players, board.getInGamePlayers());


        board.addSteps(player3, 10);
        players.clear();
        players.add(0, player3);
        players.add(1, player1);
        players.add(2, player2);
        board.refreshInGamePlayers();
        assertEquals(players, board.getInGamePlayers());


        player1.setGaveUp(true);
        players.remove(player1);
        board.refreshInGamePlayers();
        assertEquals(players, board.getInGamePlayers());
        assertTrue(board.getGaveUpPlayers().contains(player1));
        assertEquals(1, board.getGaveUpPlayers().size());

        for(int i = 0; i < 2; i++) {
            assertEquals(i, players.get(i).getPosition());
        }

        assertTrue(player3.getStep() - player2.getStep() < board.getStepsForALap());
        board.addSteps(player3, 20);
        players.remove(player2);
        assertTrue(player3.getStep() - player2.getStep() > board.getStepsForALap());
        board.refreshInGamePlayers();
        assertEquals(players, board.getInGamePlayers());
        assertEquals(1, board.getInGamePlayers().size());
        assertTrue(board.getGaveUpPlayers().contains(player1));
        assertEquals(2, board.getGaveUpPlayers().size());
    }

    @Test
    void addSteps() throws JsonProcessingException {
        Board board = new Board(Level.LEARNING);
        board.clearInGamePlayers();

        PlayerData[] players = new PlayerData[4];
        players[0] = new PlayerData("123e4567-e89b-12d3-a456-426614174001", null, null);
        players[1] = new PlayerData("123e4567-e89b-12d3-a456-426614174002", null, null);
        players[2] = new PlayerData("123e4567-e89b-12d3-a456-426614174003", null, null);
        players[3] = new PlayerData("123e4567-e89b-12d3-a456-426614174004", null, null);

        for (int i = 0; i < 4; i++) {
            board.setPlayer(players[i], i);
        }

        int before = players[0].getStep();
        board.addSteps(players[0], 2);
        assertEquals(before + 2, players[0].getStep());

        before = players[2].getStep();
        board.addSteps(players[2], 1);
        assertEquals(before + 2, players[2].getStep());

        before = players[1].getStep();
        board.addSteps(players[1], -2);
        assertEquals(before - 3, players[1].getStep());

        assertThrows(NullPointerException.class, () -> {
            board.addSteps(null, 2);
        });
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void popTile(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        for (int i = -1; i < 153; i++) {
            if (i == -1 || i == 152) {
                int index = i;
                assertThrows(IndexOutOfBoundsException.class, () -> {
                    board.popTile(index);
                });
            } else {
                assertEquals(i, board.popTile(i).getID());
                assertNull(board.getTiles()[i]);
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void putTile(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        ArrayList<Component> components = new ArrayList<>();

        assertThrows(NullPointerException.class, () -> {
            board.putTile(null);
        });

        for (int i = 0; i < 152; i++) {
            int index = i;
            assertThrows(IllegalStateException.class, () -> {
                board.putTile(board.getTiles()[index]);
            });
        }

        for (int i = 0; i < 152; i++) {
            components.add(board.popTile(i));
        }

        Collections.shuffle(components);
        for (Component component : components) {
            board.putTile(component);
            assertEquals(component, board.getTiles()[component.getID()]);
        }

        for (int i = 0; i < 152; i++) {
            assertEquals(i, board.getTiles()[i].getID());
        }

    }
}