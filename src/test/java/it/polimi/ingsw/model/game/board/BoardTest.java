package it.polimi.ingsw.model.game.board;

import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    Field viewableTilesField = Board.class.getDeclaredField("viewableTiles");
    Field hiddenTilesField = Board.class.getDeclaredField("hiddenTiles");

    BoardTest() throws NoSuchFieldException {
    }

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

        // Check if the level is set to an unexpected value
        assertThrows(IllegalArgumentException.class, () -> {
            new Board(null);
        });
    }

    @Test
    void drawCard() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
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

    @Test
    void getDeck() throws JsonProcessingException {
        assertThrows(IllegalStateException.class, () -> {
            Board board = new Board(Level.LEARNING);
            board.getDeck(0, new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        Board board = new Board(Level.SECOND);
        assertThrows(NullPointerException.class, () -> {
            board.getDeck(0, null);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getDeck(-1, new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null));
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.getDeck(4, new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        for (int i = 0; i < 3; i++) {
            int index = i;
            assertThrows(IllegalStateException.class, () -> {
                SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.RED);
                PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, spaceShip);
                board.getDeck(index, player);
            });
        }

        assertThrows(IllegalStateException.class, () -> {
            board.getDeck(3, new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null));
        });

        for (int i = 0; i < 3; i++) {
            SpaceShip spaceShip = new SpaceShip(Level.SECOND, PlayerColor.RED);
            PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, spaceShip);
            spaceShip.placeComponent(TilesManager.getTiles().get(7), 6, 7);
            assertNotNull(board.getDeck(i, player));
        }

        Board b = new Board(Level.SECOND);
        assertEquals(4, b.getDecks().length);
        Board b1 = new Board(Level.LEARNING);
        assertNull(b1.getDecks());
    }

    @Test
    void leaveDeck() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, new SpaceShip(Level.SECOND, PlayerColor.RED));
        player.getSpaceShip().placeComponent(new Cabin(3, null), 6, 7);
        Deck deck = board.getDeck(0, player);
        board.leaveDeck(0, player);
        assertTrue(deck.isPickable());
    }

    @Test
    void leaveDeck_whenDeckAlreadyPickable() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, new SpaceShip(Level.SECOND, PlayerColor.RED));
        assertThrows(IllegalStateException.class, () -> board.leaveDeck(0, player));
    }

    @Test
    void leaveDeck_whenLevelIsLearning() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        assertThrows(IllegalStateException.class, () -> board.leaveDeck(0, player));
    }

    @Test
    void leaveDeck_forInvalidIndex() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, new SpaceShip(Level.SECOND, PlayerColor.RED));
        assertThrows(IndexOutOfBoundsException.class, () -> board.leaveDeck(-1, player));
        assertThrows(IndexOutOfBoundsException.class, () -> board.leaveDeck(4, player));
    }

    @Test
    void leaveDeck_whenPlayerIsNull() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        assertThrows(NullPointerException.class, () -> board.leaveDeck(0, null));
    }

    @Test
    void putTile_addsTileToViewableTiles() throws JsonProcessingException, IllegalAccessException {
        viewableTilesField.setAccessible(true);
        Board board = new Board(Level.SECOND);
        Component tile = TilesManager.getTiles().get(0);
        board.putTile(tile);
        assertTrue(((ArrayList<Component>) viewableTilesField.get(board)).contains(tile));
    }

    @Test
    void putTile_whenTileAlreadyInBoard() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        Component tile = TilesManager.getTiles().get(0);
        board.putTile(tile);
        assertThrows(IllegalStateException.class, () -> board.putTile(tile));
    }

    @Test
    void putTile_whenTileIsNull() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        assertThrows(NullPointerException.class, () -> board.putTile(null));
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void setPlayer(Level level) throws JsonProcessingException {
        Board board = new Board(level);
        PlayerData[] players = new PlayerData[4];
        players[0] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        players[1] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", null, null);
        players[3] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174004", null, null);

        assertThrows(NullPointerException.class, () -> {
            board.setPlayer(players[2], 0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], -1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            board.setPlayer(players[3], 4);
        });

        players[2] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174003", null, null);

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

    @Test
    void updateInGamePlayers() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        ArrayList<PlayerData> players = new ArrayList<>(3);

        PlayerData player1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        PlayerData player2 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", null, null);
        PlayerData player3 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174003", null, null);

        players.add(0, player1);
        players.add(1, player2);
        players.add(2, player3);

        board.setPlayer(player1, 0);
        board.setPlayer(player2, 1);
        board.setPlayer(player3, 2);
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
        players[0] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        players[1] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", null, null);
        players[2] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174003", null, null);
        players[3] = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174004", null, null);

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
    void popTile(Level level) throws JsonProcessingException, IllegalAccessException {
        viewableTilesField.setAccessible(true);
        hiddenTilesField.setAccessible(true);

        Board board = new Board(level);
        Component tile = TilesManager.getTiles().getFirst();
        board.putTile(tile);
        assertEquals(tile, board.popTile(tile.getID()));
        assertFalse(((ArrayList<Component>) viewableTilesField.get(board)).contains(tile));

        Board board1 = new Board(level);
        Component tile1 = board.popTile(-1);
        assertNotNull(tile1);
        assertFalse(((ArrayList<Component>) hiddenTilesField.get(board1)).contains(tile1));

    }

    @Test
    void addInGamePlayers() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        board.addInGamePlayers(player);
        assertTrue(board.getInGamePlayers().contains(player));
    }

    @Test
    void removeInGamePlayer() throws JsonProcessingException {
        Board board = new Board(Level.SECOND);
        PlayerData player = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174001", null, null);
        board.addInGamePlayers(player);
        board.removeInGamePlayer(player);
        assertFalse(board.getInGamePlayers().contains(player));
    }
}