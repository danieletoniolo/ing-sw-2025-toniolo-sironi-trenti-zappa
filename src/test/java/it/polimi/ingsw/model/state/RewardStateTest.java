package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Planets;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.ConnectorType;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RewardStateTest {
    Field scoresField = RewardState.class.getDeclaredField("scores");
    Field internalStateField = RewardState.class.getDeclaredField("internalState");
    Field levelField = RewardState.class.getDeclaredField("level");
    RewardState state;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th;

    RewardStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        scoresField.setAccessible(true);
        internalStateField.setAccessible(true);
        levelField.setAccessible(true);

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

        state = new RewardState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void getCurrentPlayer(){
        assertThrows(SynchronousStateException.class, () -> state.getCurrentPlayer());
    }

    @Test
    void entry_initializesScoresWithPlayerCoins() throws IllegalAccessException {
        state.entry();
        for (PlayerData player : state.board.getInGamePlayers()) {
            assertEquals(player.getCoins(), ((Map<PlayerData, Integer>) scoresField.get(state)).get(player));
        }
    }

    @Test
    void execute() throws IllegalAccessException {
        PlayerData player1 = state.players.getFirst();
        PlayerData player2 = state.players.get(1);
        PlayerData player3 = state.players.get(2);
        PlayerData player4 = state.players.get(3);

        ConnectorType[] connectors = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        player1.getSpaceShip().placeComponent(s, 6,7);
        player1.getSpaceShip().getStorage(2).addGood(new Good(GoodType.GREEN));
        player1.getSpaceShip().getStorage(2).addGood(new Good(GoodType.YELLOW));

        Storage s1 = new Storage(3, connectors, true, 2);
        player2.getSpaceShip().getLostComponents().add(s1);
        int lostComponents = player2.getSpaceShip().getLostComponents().size();
        int reservedComponents = player2.getSpaceShip().getReservedComponents().size();

        state.entry();

        //TODO: Mettete i crediti in verso opposto o ordine casuale
        int initialScore1 = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player1);
        int initialScore2 = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2);
        int initialScore3 = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player3);
        int initialScore4 = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player4);
        state.execute(player4);
        assertEquals(RewardState.EndInternalState.BEST_LOOKING_SHIP, internalStateField.get(state));
        System.out.println(((Map<PlayerData, Integer>) scoresField.get(state)).get(player1));
        //assertEquals(initialScore1 + 8, ((Map<PlayerData, Integer>) scoresField.get(state)).get(player1)); //Level Second
        //assertEquals(initialScore2 + 6, ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2));
        //assertEquals(initialScore3 + 4, ((Map<PlayerData, Integer>) scoresField.get(state)).get(player3));
        //assertEquals(initialScore4 + 2, ((Map<PlayerData, Integer>) scoresField.get(state)).get(player4));

        int initialScore = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2);
        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        assertEquals(initialScore + 2 * ((Level) levelField.get(state)).getValue(), ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2));
        assertEquals(RewardState.EndInternalState.SALE_OF_GOODS, internalStateField.get(state));

        //TODO: Non mettiamo i goods nella nave generale, ma solo nel singolo storage
        initialScore = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player1);
        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        assertEquals(RewardState.EndInternalState.LOSSES, internalStateField.get(state));
        assertEquals(Math.round((float) player1.getSpaceShip().getGoodsValue() / 2) + initialScore, ((Map<PlayerData, Integer>) scoresField.get(state)).get(player1));

        initialScore = ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2);
        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        System.out.println(((Map<PlayerData, Integer>) scoresField.get(state)).get(player2));
        assertEquals(initialScore - (lostComponents + reservedComponents), ((Map<PlayerData, Integer>) scoresField.get(state)).get(player2));
    }

    @Test
    void exit_withPlayerInWaitingState_or_withPlayerInPlayingState() {
        state.playersStatus.replace(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());

        state.playersStatus.replace(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.PLAYING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }
}