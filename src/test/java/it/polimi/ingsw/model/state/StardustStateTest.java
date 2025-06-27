package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.ConnectorType;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StardustStateTest {
    StardustState state;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
        @Override
        public void triggerEndGame() {

        }
    };
    StateTransitionHandler th;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        state = new StardustState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void execute_withNoExposedConnectors() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage s0 = new Storage(2, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY}, true, 2);
        Storage s1 = new Storage(3, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.TRIPLE}, true, 2);
        Storage s2 = new Storage(4, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        Storage s3 = new Storage(5, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);

        player.getSpaceShip().placeComponent(s0, 5,6);
        player.getSpaceShip().placeComponent(s1, 6,5);
        player.getSpaceShip().placeComponent(s2, 7,6);
        player.getSpaceShip().placeComponent(s3, 6,7);

        state.execute(player);

        assertEquals(6, state.board.getInGamePlayers().getFirst().getStep());
    }

    @Test
    void execute_withExposedConnectors() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage s0 = new Storage(2, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY}, true, 2);
        Storage s1 = new Storage(3, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.TRIPLE}, true, 2);
        Storage s2 = new Storage(4, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        Storage s3 = new Storage(5, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);

        player.getSpaceShip().placeComponent(s0, 5,6);
        player.getSpaceShip().placeComponent(s1, 6,5);
        player.getSpaceShip().placeComponent(s2, 7,6);
        player.getSpaceShip().placeComponent(s3, 6,7);

        state.execute(player);

        assertEquals(4, state.board.getInGamePlayers().getFirst().getStep());
    }

    @Test
    void execute_withPlayingPlayer_updatesStatusToPlayed() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        PlayerData player1 = state.board.getInGamePlayers().get(1);
        state.playersStatus.put(player1.getColor(), State.PlayerStatus.WAITING);

        state.execute(player);
        state.execute(player1);

        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player1.getColor()));
    }
}