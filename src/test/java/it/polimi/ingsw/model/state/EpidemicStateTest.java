package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicStateTest {
    EpidemicState state;
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

        state = new EpidemicState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void entry_triggersCurrentPlayerEventForAllPlayers() {
        assertDoesNotThrow(() -> state.entry());
    }

    @Test
    void execute_withAlien() {
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Cabin cabin1 = new Cabin(2, connector);
        Cabin cabin2 = new Cabin(3, connector);
        LifeSupportPurple lsp = new LifeSupportPurple(4, connector);
        player.getSpaceShip().placeComponent(cabin1, 6,7);
        player.getSpaceShip().placeComponent(cabin2, 6,8);
        player.getSpaceShip().placeComponent(lsp, 6,9);
        cabin2.isValid();
        player.getSpaceShip().getCabin(152).addCrewMember();
        cabin1.addCrewMember();
        cabin2.addPurpleAlien();

        state.execute(player);

        assertEquals(1, cabin1.getCrewNumber());
        assertEquals(0, cabin2.getCrewNumber());
    }

    @Test
    void execute() {
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Cabin cabin1 = new Cabin(2, connector);
        Storage s = new Storage(3, connector, true, 3);
        Cabin cabin2 = new Cabin(4, connector);
        LifeSupportPurple lsp = new LifeSupportPurple(5, connector);
        Cabin cabin3 = new Cabin(6, connector);
        Cabin cabin4 = new Cabin(7, connector);
        player.getSpaceShip().placeComponent(cabin1, 6,7);
        player.getSpaceShip().placeComponent(s, 6,8);
        player.getSpaceShip().placeComponent(cabin2, 7,8);
        player.getSpaceShip().placeComponent(lsp, 6,5);
        player.getSpaceShip().placeComponent(cabin3, 6,4);
        cabin3.isValid();
        player.getSpaceShip().placeComponent(cabin4, 5,6);
        player.getSpaceShip().getCabin(152).addCrewMember();
        cabin1.addCrewMember();
        cabin2.addCrewMember();
        cabin3.addPurpleAlien();
        cabin4.addCrewMember();

        state.execute(player);

        assertEquals(1, player.getSpaceShip().getCabin(7).getCrewNumber());
        assertEquals(1, player.getSpaceShip().getCabin(152).getCrewNumber());
        assertEquals(1, player.getSpaceShip().getCabin(2).getCrewNumber());
        assertEquals(2, player.getSpaceShip().getCabin(4).getCrewNumber());
        assertEquals(1, player.getSpaceShip().getCabin(6).getCrewNumber());
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
    void execute_withPlayingPlayer() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        PlayerData player1 = state.board.getInGamePlayers().get(1);
        state.playersStatus.put(player1.getColor(), State.PlayerStatus.WAITING);

        state.execute(player);
        state.execute(player1);

        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player1.getColor()));
    }

    @Test
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
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
        assertFalse(state.played);
    }

    @Test
    void getCurrentPlayer() {
        assertThrows(SynchronousStateException.class, () -> state.getCurrentPlayer());
    }
}