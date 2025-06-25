package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.Cabin;
import it.polimi.ingsw.model.spaceship.ConnectorType;
import it.polimi.ingsw.model.spaceship.LifeSupportBrown;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrewStateTest {
    CrewState state;
    PlayerData p0;
    Board board;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        state = new CrewState(board, ecb, th);
    }

    @Test
    void manageCrewMember_removesCrewWhenModeIsRemoveAndCabinExists() {
        ConnectorType[] c = {ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new LifeSupportBrown(1, c), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(2, c), 6, 8);
        player.getSpaceShip().getCabin(2).isValid();
        player.getSpaceShip().addCrewMember(2, true, false);
        player.getSpaceShip().addCrewMember(152, false, false);

        assertDoesNotThrow(() -> state.manageCrewMember(player, 1, 0, 152));
        assertEquals(1, player.getSpaceShip().getCrewNumber());
        assertDoesNotThrow(() -> state.manageCrewMember(player, 0, 0, 152));
        assertEquals(3, player.getSpaceShip().getCrewNumber());
    }

    @Test
    void manageCrewMember_addsCrewWhenModeIsAddAndCabinExists() {
        ConnectorType[] c = {ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new LifeSupportBrown(1, c), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(2, c), 6, 8);
        player.getSpaceShip().getCabin(2).isValid();

        assertDoesNotThrow(() -> state.manageCrewMember(player, 0, 0, 152));
        assertEquals(2, player.getSpaceShip().getCrewNumber());
        assertDoesNotThrow(() -> state.manageCrewMember(player, 0, 1, 2));
        assertEquals(3, player.getSpaceShip().getCrewNumber());
    }

    @Test
    void manageCrewMember_whenCabinDoesNotExist() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalArgumentException.class, () -> state.manageCrewMember(player, 0, 0, 999));
    }

    @Test
    void manageCrewMember_whenAddingToCenterCabin() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.manageCrewMember(player, 0, 1, 152));
    }

    @Test
    void getCurrentPlayer() {
        assertThrows(SynchronousStateException.class, () -> state.getCurrentPlayer());
    }

    @Test
    void synchronousState() {
        SynchronousStateException exception = new SynchronousStateException();
        SynchronousStateException e = new SynchronousStateException("Text", new Throwable());
    }

    @Test
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.play(player);
        assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @Test
    void play_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @Test
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withPlayingPlayer_updatesStatusToPlayed() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        player.getSpaceShip().addCrewMember(152, false, false);
        PlayerData player1 = state.board.getInGamePlayers().get(1);
        state.playersStatus.put(player1.getColor(), State.PlayerStatus.WAITING);
        player1.getSpaceShip().addCrewMember(154, false, false);

        state.execute(player);
        state.execute(player1);

        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player1.getColor()));
    }

    @Test
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @Test
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @Test
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void exit_withNoPlayers() {
        state.players.clear();
        state.playersStatus.clear();

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @Test
    void entry() {
        assertDoesNotThrow(() -> state.entry());
        assertFalse(state.played);
    }

    @Test
    void entry_withLearningLevel() throws JsonProcessingException {
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, new SpaceShip(Level.LEARNING, PlayerColor.RED));
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, new SpaceShip(Level.LEARNING, PlayerColor.GREEN));
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, new SpaceShip(Level.LEARNING, PlayerColor.YELLOW));

        Board bL = new Board(Level.LEARNING);
        bL.clearInGamePlayers();
        bL.setPlayer(p0, 0);
        bL.setPlayer(p1, 1);
        bL.setPlayer(p2, 2);
        bL.setPlayer(p3, 3);
        CrewState cs1 = new CrewState(bL, ecb, th);
        assertDoesNotThrow(() -> cs1.entry());

        for (PlayerData player : cs1.board.getInGamePlayers()) {
            for (Cabin cabin : player.getSpaceShip().getCabins()) {
                assertEquals(2, cabin.getCrewNumber());
            }
        }
    }

    @Test
    void execute_withPlayerInGame() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().addCrewMember(152, false, false);
        state.execute(player);
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }
}