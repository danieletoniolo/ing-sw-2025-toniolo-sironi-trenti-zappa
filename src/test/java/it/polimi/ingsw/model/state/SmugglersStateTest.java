package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Smugglers;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersStateTest {
    Field cannonStrengthField = SmugglersState.class.getDeclaredField("cannonsStrength");
    Field internalStateField = SmugglersState.class.getDeclaredField("internalState");
    Field cardField = SmugglersState.class.getDeclaredField("card");
    Field currentPenaltyLossField = SmugglersState.class.getDeclaredField("currentPenaltyLoss");
    SmugglersState state;
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
    StateTransitionHandler th = _ -> {
    };

    SmugglersStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        cannonStrengthField.setAccessible(true);
        internalStateField.setAccessible(true);
        cardField.setAccessible(true);
        currentPenaltyLossField.setAccessible(true);

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

        List<Good> good = List.of(new Good(GoodType.GREEN), new Good(GoodType.YELLOW));
        Smugglers c1 = new Smugglers(2, 1, good, 1, 1, 1);

        state = new SmugglersState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @Test
    void setGoodsToExchange_withValidExchangeData() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage s = new Storage(1, null, true, 2);
        player.getSpaceShip().placeComponent(s, 6, 7);
        List<Good> goodsToLeave = List.of(new Good(GoodType.GREEN));
        List<Good> goodsToGet = List.of(new Good(GoodType.YELLOW));
        player.getSpaceShip().exchangeGood(goodsToGet, null, 1);
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of(Triplet.with(goodsToLeave, goodsToGet, 1));

        assertDoesNotThrow(() -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void setGoodsToExchange_withInvalidInternalState() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Good> goodsToLeave = List.of(new Good(GoodType.GREEN));
        List<Good> goodsToGet = List.of(new Good(GoodType.YELLOW));
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of(Triplet.with(goodsToLeave, goodsToGet, 1));

        assertThrows(IllegalStateException.class, () -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void setGoodsToExchange_withNullPlayer() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        List<Good> goodsToLeave = List.of(new Good(GoodType.GREEN));
        List<Good> goodsToGet = List.of(new Good(GoodType.YELLOW));
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of(Triplet.with(goodsToLeave, goodsToGet, 1));

        assertThrows(NullPointerException.class, () -> state.setGoodsToExchange(null, exchangeData));
    }

    @Test
    void setGoodsToExchange_withEmptyExchangeData() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of();

        assertDoesNotThrow(() -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void setGoodsToExchange_withInvalidStorageID() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Good> goodsToLeave = List.of(new Good(GoodType.GREEN));
        List<Good> goodsToGet = List.of(new Good(GoodType.YELLOW));
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of(Triplet.with(goodsToLeave, goodsToGet, -1));

        assertThrows(IllegalArgumentException.class, () -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void swapGoods_withValidStoragesAndGoods() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage storage1 = new Storage(1, null, true, 2);
        Storage storage2 = new Storage(2, null, true, 2);
        player.getSpaceShip().placeComponent(storage1, 6, 7);
        player.getSpaceShip().placeComponent(storage2, 6, 8);
        List<Good> goods1to2 = List.of(new Good(GoodType.GREEN));
        List<Good> goods2to1 = List.of(new Good(GoodType.YELLOW));
        player.getSpaceShip().exchangeGood(goods1to2, null, 1);
        player.getSpaceShip().exchangeGood(goods2to1, null, 2);

        assertDoesNotThrow(() -> state.swapGoods(player, 1, 2, goods1to2, goods2to1));
        assertTrue(storage1.getGoods().containsAll(goods2to1));
        assertTrue(storage2.getGoods().containsAll(goods1to2));
    }

    @Test
    void swapGoods_withInvalidStorageIDs() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Good> goods1to2 = List.of(new Good(GoodType.GREEN));
        List<Good> goods2to1 = List.of(new Good(GoodType.YELLOW));

        assertThrows(IllegalArgumentException.class, () -> state.swapGoods(player, -1, 99, goods1to2, goods2to1));
    }

    @Test
    void swapGoods_withInvalidInternalState() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Good> goods1to2 = List.of(new Good(GoodType.GREEN));
        List<Good> goods2to1 = List.of(new Good(GoodType.YELLOW));

        assertThrows(IllegalStateException.class, () -> state.swapGoods(player, 1, 2, goods1to2, goods2to1));
    }

    @Test
    void swapGoods_withEmptyGoodsLists() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage storage1 = new Storage(1, null, true, 2);
        Storage storage2 = new Storage(2, null, true, 2);
        player.getSpaceShip().placeComponent(storage1, 6, 7);
        player.getSpaceShip().placeComponent(storage2, 6, 8);
        List<Good> goods1to2 = List.of();
        List<Good> goods2to1 = List.of();

        assertDoesNotThrow(() -> state.swapGoods(player, 1, 2, goods1to2, goods2to1));
    }

    @Test
    void useExtraStrength_withTypeZero() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);

        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 0, ids, batteries));
    }

    @Test
    void useExtraStrength_withInvalidType() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);

        assertThrows(IllegalArgumentException.class, () -> state.useExtraStrength(player, 99, ids, batteries));
    }

    @Test
    void useExtraStrength_withTypeOneAndValidState() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.BATTERIES_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 2), 6, 8);
        List<Integer> ids = List.of(2);
        List<Integer> batteries = List.of(3);

        assertDoesNotThrow(() -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @Test
    void useExtraStrength_withTypeOneAndInvalidState() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);

        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @Test
    void setPenaltyLoss_withValidGoodsLoss() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage s = new Storage(1, null, true, 2);
        player.getSpaceShip().placeComponent(s, 6, 7);
        player.getSpaceShip().exchangeGood(List.of(new Good(GoodType.YELLOW)), null, 1);
        List<Integer> penaltyLoss = List.of(1);

        assertDoesNotThrow(() -> state.setPenaltyLoss(player, 0, penaltyLoss));
    }

    @Test
    void setPenaltyLoss_withValidBatteriesLoss() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.BATTERIES_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 7);
        List<Integer> penaltyLoss = List.of(1);

        assertDoesNotThrow(() -> state.setPenaltyLoss(player, 1, penaltyLoss));
    }

    @Test
    void setPenaltyLoss_withInvalidType() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> penaltyLoss = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(player, 3, penaltyLoss));
    }

    @Test
    void setPenaltyLoss_withNullPlayer() throws IllegalAccessException {
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        List<Integer> penaltyLoss = List.of(1, 2);

        assertThrows(NullPointerException.class, () -> state.setPenaltyLoss(null, 0, penaltyLoss));
    }

    @Test
    void entry_initializesCannonStrengthForAllPlayers() throws IllegalAccessException {
        state.entry();
        for (PlayerData player : state.board.getInGamePlayers()) {
            assertEquals(player.getSpaceShip().getSingleCannonsStrength(), ((Map<PlayerData, Float>) cannonStrengthField.get(state)).get(player));
        }
    }

    @Test
    void execute_validPlayerWithSufficientCannonStrength() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        internalStateField.set(state, SmugglersState.SmugglerInternalState.ENEMY_DEFEAT);

        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s1 = new Storage(2, connectors, true, 2);
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(new Cannon(3, connectors, 1), 6, 5);
        player.getSpaceShip().placeComponent(new Cannon(4, connectors, 1), 6, 4);
        ArrayList<Good> add = new ArrayList<>();
        add.add(new Good(GoodType.GREEN));
        ArrayList<Good> remove = new ArrayList<>();
        player.getSpaceShip().exchangeGood(add, remove, 2);
        state.entry();
        state.execute(player);
        assertEquals(SmugglersState.SmugglerInternalState.GOODS_REWARD, internalStateField.get(state));
    }

    @Test
    void execute_validPlayerWithExactCannonStrength() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cannon(3, connectors, 1), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.entry();
        state.execute(player);
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_validPlayerWithInsufficientCannonStrength() throws IllegalAccessException {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.entry();
        state.execute(player);
        assertEquals(SmugglersState.SmugglerInternalState.GOODS_PENALTY, internalStateField.get(state));
    }

    @Test
    void execute_nullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @Test
    void execute_goodsReward() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        for(PlayerData p: state.board.getInGamePlayers()) {
            state.playersStatus.put(p.getColor(), State.PlayerStatus.PLAYING);
        }
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_REWARD);

        int initialStep = player.getStep();
        state.execute(player);
        assertTrue(initialStep - ((Smugglers) cardField.get(state)).getFlightDays() >= player.getStep());
        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_goodsPenalty() throws IllegalAccessException {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        internalStateField.set(state, SmugglersState.SmugglerInternalState.GOODS_PENALTY);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.entry();
        state.execute(player);

        currentPenaltyLossField.set(state, 1);
        assertEquals(SmugglersState.SmugglerInternalState.BATTERIES_PENALTY, internalStateField.get(state));
    }

    @Test
    void execute_batteriesPenalty() throws IllegalAccessException {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.board.getInGamePlayers().getFirst();
        internalStateField.set(state, SmugglersState.SmugglerInternalState.BATTERIES_PENALTY);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.entry();
        state.execute(player);

        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
        assertEquals(SmugglersState.SmugglerInternalState.ENEMY_DEFEAT, internalStateField.get(state));
    }

    @Test
    void exit_noPlayersPlayed() {
        state.entry();
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.replace(player.getColor(), State.PlayerStatus.SKIPPED);
        }
        int[] initialSteps = new int[state.board.getInGamePlayers().size()];
        for(int i = 0; i < state.board.getInGamePlayers().size(); i++) {
            initialSteps[i] = state.board.getInGamePlayers().get(i).getStep();
        }
        state.exit();
        for(int i = 0; i < state.board.getInGamePlayers().size(); i++) {
            assertEquals(initialSteps[i], state.board.getInGamePlayers().get(i).getStep());
        }
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
    }

    @RepeatedTest(5)
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
}