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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuildingStateTest {
    Field timerDurationField = BuildingState.class.getDeclaredField("timerDuration");
    Field timerRunningField = BuildingState.class.getDeclaredField("timerRunning");
    Field playersHandQueueField = BuildingState.class.getDeclaredField("playersHandQueue");
    Field numberOfFlipsField = BuildingState.class.getDeclaredField("numberOfTimerFlips");

    Field inGamePlayersField = Board.class.getDeclaredField("inGamePlayers");

    BuildingState state;
    Board board;
    PlayerData p0;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th = _ -> {
    };

    BuildingStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        timerDurationField.setAccessible(true);
        timerRunningField.setAccessible(true);
        playersHandQueueField.setAccessible(true);
        numberOfFlipsField.setAccessible(true);
        inGamePlayersField.setAccessible(true);

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

        state = new BuildingState(board, ecb, th);
    }

    @Test
    void getCurrentPlayer() {
        assertThrows(SynchronousStateException.class, () -> {state.getCurrentPlayer();});
    }

    @Test
    void cheatCode_withValidPlayerAndShipIndex_0() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int validShipIndex = 0;

        assertDoesNotThrow(() -> state.cheatCode(player, validShipIndex));
    }

    @Test
    void cheatCode_withValidPlayerAndShipIndex_1() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int validShipIndex = 1;

        assertDoesNotThrow(() -> state.cheatCode(player, validShipIndex));
    }

    @Test
    void cheatCode_withValidPlayerAndShipIndex_2() throws JsonProcessingException {
        Board b = new Board(Level.LEARNING);
        SpaceShip s = new SpaceShip(Level.LEARNING, PlayerColor.BLUE);
        p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, s);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, s);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, s);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, s);

        b.clearInGamePlayers();
        b.setPlayer(p0, 0);
        b.setPlayer(p1, 1);
        b.setPlayer(p2, 2);
        b.setPlayer(p3, 3);
        BuildingState bs = new BuildingState(b, ecb, th);
        PlayerData player = bs.board.getInGamePlayers().getFirst();
        int validShipIndex = 2;

        assertDoesNotThrow(() -> bs.cheatCode(player, validShipIndex));
    }

    @Test
    void cheatCode_withInvalidShipIndex() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int invalidShipIndex = -1;

        assertThrows(IllegalArgumentException.class, () -> state.cheatCode(player, invalidShipIndex));
    }

    @Test
    void useDeck_withValidUsageAndDeckIndex() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}), 6, 7);

        assertDoesNotThrow(() -> state.useDeck(player, 0, 1));
        assertDoesNotThrow(() -> state.useDeck(player, 1, 1));
    }

    @Test
    void useDeck_withPlayerFinishedBuilding() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.useDeck(player, 0, 1));
    }

    @Test
    void useDeck_withPlayerNotPlacedAnyTile() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.useDeck(player, 0, 1));
    }

    @Test
    void flipTimer_withLearningLevel() throws JsonProcessingException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        PlayerData p = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        Board b = new Board(Level.LEARNING);
        b.clearInGamePlayers();
        b.setPlayer(p, 0);
        b.refreshInGamePlayers();
        BuildingState s = new BuildingState(b, ecb, th);
        assertThrows(IllegalStateException.class, () -> s.flipTimer(p));
    }

    @Test
    void flipTimer_whenTimerAlreadyRunning() {
        state.flipTimer(p0);
        assertThrows(IllegalStateException.class, () -> state.flipTimer(p0));
    }

    @Test
    void flipTimer_withValidFirstFlip() throws IllegalAccessException {
        assertDoesNotThrow(() -> state.flipTimer(p0));
        assertTrue((boolean) timerRunningField.get(state));
    }

    @Test
    void flipTimer_withThirdFlipAndPlayerNotFinishedBuilding(){
        state.flipTimer(p0);
        try{
            Thread.sleep((long) timerDurationField.get(state) + 100);
        } catch (InterruptedException _) {
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        state.flipTimer(p0);
        assertThrows(IllegalStateException.class, () -> state.flipTimer(p0));
    }

    @Test
    void flipTimer_withThirdFlipAndPlayerFinishedBuilding() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.flipTimer(player);
        try{
            Thread.sleep((long) timerDurationField.get(state) + 100);
        } catch (InterruptedException _) {
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        state.flipTimer(player);
        try{
            Thread.sleep((long) timerDurationField.get(state) + 100);
        } catch (InterruptedException _) {
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.put(p.getColor(), State.PlayerStatus.PLAYED);
        }
        assertDoesNotThrow(() -> state.flipTimer(player));
        assertTrue((boolean) timerRunningField.get(state));
    }

    @Test
    void placeMarker_withInvalidPosition() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int invalidPosition = -1;
        assertThrows(IllegalStateException.class, () -> state.placeMarker(player, invalidPosition));
    }

    @Test
    void placeMarker_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        PlayerData newPlayer = new PlayerData("p0", nonExistentUUID.toString(), PlayerColor.BLUE, ship);
        int validPosition = 1;
        assertThrows(IllegalStateException.class, () -> state.placeMarker(newPlayer, validPosition));
    }

    @Test
    void placeTile_placesTileInBoardWhenToWhereIsZero() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 0, 0, 0));
        assertNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void placeTile_placesTileInReserveWhenToWhereIsOne() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 1, 0, 0));
        assertTrue(player.getSpaceShip().getReservedComponents().contains(component));
        assertNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void placeTile_placesTileInSpaceShipWhenToWhereIsTwo() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 2, 6, 7));
        assertNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void placeTile_whenPlayerHasNoTileInHand() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 2, 6, 7));
    }

    @Test
    void placeTile_whenPlayerHasFinishedBuilding() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 2, 6, 7));
    }

    @Test
    void placeTile_whenToWhereIsInvalid() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 3, 6, 7));
    }

    @Test
    void rotateTile_rotatesTileClockwiseWhenPlayerHasTileInHand() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);

        assertDoesNotThrow(() -> state.rotateTile(player));
        assertEquals(1, component.getClockwiseRotation());
    }

    @Test
    void rotateTile_whenPlayerHasNoTileInHand() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.rotateTile(player));
    }

    @Test
    void rotateTile_whenPlayerHasFinishedBuilding() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.rotateTile(player));
    }

    @Test
    void pickTile_picksTileFromBoardWhenValidTileIDProvided() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        board.putTile(new Cabin(1, c));

        assertDoesNotThrow(() -> state.pickTile(player, 0, 1));
    }

    @Test
    void pickTile_throwsExceptionWhenInvalidFromWhereProvided() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int invalidFromWhere = 3;

        assertThrows(IllegalStateException.class, () -> state.pickTile(player, invalidFromWhere, 1));
    }

    @Test
    void pickTile_throwsExceptionWhenPlayerHasFinishedBuilding() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.pickTile(player, 0, 1));
    }

    @Test
    void pickTile_throwsExceptionWhenTileIDDoesNotMatch() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.board.putTile(component);

        assertThrows(IndexOutOfBoundsException.class, () -> state.pickTile(player, 0, 999));
    }

    @RepeatedTest(100)
    void pickTile_fromBoard_triggersCorrectEventForEngine() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        ((Map<PlayerColor, Component>) playersHandQueueField.get(state)).put(player.getColor(), null);

        assertDoesNotThrow(() -> state.pickTile(player, 0, -1));
        assertNotNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void pickTile_fromReserve() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        player.getSpaceShip().putReserveComponent(component);

        assertDoesNotThrow(() -> state.pickTile(player, 1, 1));
        assertNotNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void pickTile_fromSpaceship() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        player.getSpaceShip().placeComponent(component, 6, 7);

        assertDoesNotThrow(() -> state.pickTile(player, 2, 1));
        assertNotNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).get(player.getColor()));
    }

    @Test
    void entry_initializesStateCorrectly() throws IllegalAccessException {
        state.entry();
        assertNotNull(((Map<PlayerColor, Component>) playersHandQueueField.get(state)));
        assertTrue((boolean) timerRunningField.get(state));
    }

    @Test
    void execute_withValidPlayer() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertDoesNotThrow(() -> state.execute(player));
    }

    @Test
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @Test
    void exit_executesWithoutExceptions() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }
        assertDoesNotThrow(() -> state.exit());
    }

    @Test
    void exit_resetsStateCorrectly() throws IllegalAccessException {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }
        state.exit();
        assertFalse((boolean) timerRunningField.get(state));
        assertTrue(((Map<PlayerColor, Component>) playersHandQueueField.get(state)).isEmpty());
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
}