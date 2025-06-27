package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.OpenSpace;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceStateTest {
    Field statsField = OpenSpaceState.class.getDeclaredField("stats");
    OpenSpaceState state;
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

    OpenSpaceStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        statsField.setAccessible(true);

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

        state = new OpenSpaceState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void useExtraStrength_updatesStatsCorrectly() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(2, null, 1), 6, 8);
        List<Integer> engineIDs = List.of(1);
        List<Integer> batteryIDs = List.of(2);
        float initialStrength = player.getSpaceShip().getEnginesStrength(engineIDs);
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 0.0f);

        assertDoesNotThrow(() -> state.useExtraStrength(player, 0, engineIDs, batteryIDs));
        assertEquals(initialStrength, ((Map<PlayerData, Float>) statsField.get(state)).get(player));
    }

    @Test
    void useExtraStrength_withInvalidType() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> engineIDs = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.useExtraStrength(player, 99, engineIDs, List.of()));
    }

    @Test
    void useExtraStrength_withDoubleCannons() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> cannonIDs = List.of(3, 4);

        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 1, cannonIDs, List.of()));
    }

    @Test
    void useExtraStrength_withEmptyEngineIDs_doesNotUpdateStats() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 0.0f);

        assertDoesNotThrow(() -> state.useExtraStrength(player, 0, List.of(), List.of()));
        assertEquals(0.0f, ((Map<PlayerData, Float>) statsField.get(state)).get(player));
    }

    @Test
    void entry_withPlayersHavingSingleEngines() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6)
        );

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                {
                    try {
                        assertEquals(player.getSpaceShip().getSingleEnginesStrength(), ((Map<PlayerData, Float>) statsField.get(state)).get(player));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Test
    void entry_withPlayersHavingBrownAlien() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportBrown(2, connectors), 7, 6)
        );
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, true, false);
        state.players.get(1).getSpaceShip().getCabin(154).isValid();
        state.players.get(1).getSpaceShip().addCrewMember(154, true, false);
        state.players.get(2).getSpaceShip().getCabin(153).isValid();
        state.players.get(2).getSpaceShip().addCrewMember(153, true, false);
        state.players.get(3).getSpaceShip().getCabin(155).isValid();
        state.players.get(3).getSpaceShip().addCrewMember(155, true, false);

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                {
                    try {
                        assertEquals(0.0f, ((Map<PlayerData, Float>) statsField.get(state)).get(player));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Test
    void execute_withPositiveStats() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 5.0f);

        assertDoesNotThrow(() -> state.execute(player));
        assertFalse(player.hasGivenUp());
    }

    @Test
    void execute_withZeroStats() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.entry();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, (float) 0);

        assertDoesNotThrow(() -> state.execute(player));
    }

    @Test
    void execute_withNullStats() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, null);

        assertThrows(NullPointerException.class, () -> state.execute(player));
    }

    @Test
    void execute_withPlayerNotInStats() {
        PlayerData player = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174005", PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));

        assertThrows(NullPointerException.class, () -> state.execute(player));
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
    }
}