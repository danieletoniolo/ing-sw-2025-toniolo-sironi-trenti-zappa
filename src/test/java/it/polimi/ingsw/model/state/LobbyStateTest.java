package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Direction;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.cards.hits.HitType;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LobbyStateTest {
    LobbyState state;
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
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.refreshInGamePlayers();

        state = new LobbyState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void getCurrentPlayer_whenAllPlayersHavePlayed() {
        for(PlayerData player : state.board.getInGamePlayers()) {
            if(player != null) {
                state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
            }
        }
        assertThrows(IllegalStateException.class, () -> state.getCurrentPlayer());
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
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            if(player != null) {
                state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
            }
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @Test
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            if(player != null) {
                state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
            }
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
    void execute_withPlayerInGame() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.execute(player);
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withPlayerNotInGame() {
        PlayerData player = new PlayerData("player", "123e4567-e89b-12d3-a456-426614174007", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        state.execute(player);
        assertNull(state.playersStatus.get(player.getColor()));
    }

    @Test
    void manageLobby_addsPlayerToLobby() {
        PlayerData player = new PlayerData("player", "123e4567-e89b-12d3-a456-426614174007", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        state.manageLobby(player, 0);
        assertTrue(state.players.contains(player));
        assertTrue(state.board.getInGamePlayers().contains(player));
    }

    @Test
    void manageLobby_removesPlayerFromLobby() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.manageLobby(player, 1);
        assertFalse(state.board.getInGamePlayers().contains(player));
    }

    @Test
    void manageLobby_forInvalidType() {
        PlayerData player = new PlayerData("player", "123e4567-e89b-12d3-a456-426614174007", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        assertThrows(IllegalArgumentException.class, () -> state.manageLobby(player, 2));
    }

    @Test
    void startGame() throws JsonProcessingException {
        LobbyState lsL = new LobbyState(new Board(Level.LEARNING), ecb, th);

        LocalTime startTime = LocalTime.now();
        int timerDuration = 1000;
        assertDoesNotThrow(() -> lsL.startGame(startTime, timerDuration));
        assertNotNull(lsL.board.getShuffledDeck());

        LocalTime startTime1 = LocalTime.now();
        state.board.getShuffledDeck().add(new Slavers(2, 1, 1, 1, 1, 1));
        state.board.getShuffledDeck().add(new Pirates(2, 2, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)), 1, 1, 1));
        state.startGame(startTime1, timerDuration);
        for (PlayerData player : state.players) {
            assertNotNull(player.getSpaceShip().getComponent(6, 6));
        }
    }




    @Test
    void manageLobby_withValidPlayerAndJoinType() throws JsonProcessingException {
        PlayerData player = new PlayerData("player1", UUID.randomUUID().toString(), PlayerColor.RED, new SpaceShip(Level.SECOND, PlayerColor.RED));
        Board board = new Board(Level.SECOND);
        State state = new State(board, ecb, th) {};

        assertThrows(IllegalStateException.class, () -> state.manageLobby(player, 0));
        assertThrows(IllegalStateException.class, () -> {state.setPenaltyLoss(player, 0, null);});
    }
}