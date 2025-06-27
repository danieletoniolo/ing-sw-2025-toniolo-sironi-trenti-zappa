package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.AbandonedStation;
import it.polimi.ingsw.model.game.board.*;
import it.polimi.ingsw.model.good.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationStateTest {
    Field cardField = AbandonedStationState.class.getDeclaredField("card");
    AbandonedStationState state;
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

    AbandonedStationStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        List<Good> goods = new ArrayList<>();
        goods.add(new Good(GoodType.YELLOW));
        goods.add(new Good(GoodType.BLUE));
        goods.add(new Good(GoodType.GREEN));
        AbandonedStation c1 = new AbandonedStation(2, 3, 3, 1, goods);

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

        state = new AbandonedStationState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @Test
    void setGoodsToExchange_correctly() {
        Good g = new Good(GoodType.YELLOW);
        ArrayList<Good> goodsToGive = new ArrayList<>(List.of(new Good(GoodType.YELLOW), new Good(GoodType.BLUE)));
        ArrayList<Good> goodsToReceive = new ArrayList<>(List.of(g));
        Triplet<List<Good>, List<Good>, Integer> exchange = new Triplet<>(goodsToGive, goodsToReceive, 1);
        ArrayList<Triplet<List<Good>, List<Good>, Integer>> exchangeData = new ArrayList<>(List.of(exchange));
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 2), 6, 7);
        player.getSpaceShip().exchangeGood(List.of(g), null, 1);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        assertDoesNotThrow(() -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void swapGoods(){
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        Good g1 = new Good(GoodType.YELLOW);
        Good g2 = new Good(GoodType.BLUE);
        Good g3 = new Good(GoodType.GREEN);
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 2), 6, 7);
        player.getSpaceShip().placeComponent(new Storage(2, null, true, 2), 6, 8);
        player.getSpaceShip().exchangeGood(List.of(g1, g2), null, 1);
        player.getSpaceShip().exchangeGood(List.of(g3), null, 2);

        List<Good> goods1to2 = new ArrayList<>(List.of(g1));
        List<Good> goods2to1 = new ArrayList<>(List.of(g3));
        assertDoesNotThrow(() -> state.swapGoods(player, 1, 2, goods1to2, goods2to1));
        assertEquals(2, player.getSpaceShip().getStorage(1).getGoods().size());
        assertEquals(1, player.getSpaceShip().getStorage(2).getGoods().size());
    }

    @Test
    void setGoodsToExchange_whenPlayerNotPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.WAITING);
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void entry() {
        assertDoesNotThrow(() -> state.entry());
    }

    @Test
    void execute_updatesPlayerStatusToPlayedWhenPlayerIsPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        state.execute(player);

        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_performsGoodsExchange() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        ArrayList<Good> g1 = new ArrayList<>(List.of(new Good(GoodType.YELLOW)));
        player.getSpaceShip().placeComponent(s, 6, 7);
        player.getSpaceShip().exchangeGood(g1, null, 2);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        ArrayList<Good> goodsToReceive = new ArrayList<>(List.of(new Good(GoodType.BLUE)));
        Triplet<List<Good>, List<Good>, Integer> exchange = new Triplet<>(goodsToReceive, g1, 2);
        state.setGoodsToExchange(player, new ArrayList<>(List.of(exchange)));
        state.execute(player);

        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(goodsToReceive, player.getSpaceShip().getStorage(2).getGoods());
    }

    @Test
    void execute_withWaitingPlayer() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.WAITING);
        state.execute(player);

        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @Test
    void execute_withNoExchangeData() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);

        state.setGoodsToExchange(player, new ArrayList<>());

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void exit_withAllPlayersPlayed() throws IllegalAccessException {
        cardField.setAccessible(true);

        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertEquals(6 - ((AbandonedStation) cardField.get(state)).getFlightDays(), state.board.getInGamePlayers().get(0).getStep());
        assertEquals(3, state.board.getInGamePlayers().get(1).getStep());
        assertEquals(1, state.board.getInGamePlayers().get(2).getStep());
        assertEquals(0, state.board.getInGamePlayers().get(3).getStep());
    }

    @Test
    void exit_withWaitingOrPlayingPlayer() {
        state.playersStatus.put(state.board.getInGamePlayers().get(0).getColor(), State.PlayerStatus.PLAYED);
        state.playersStatus.put(state.board.getInGamePlayers().get(1).getColor(), State.PlayerStatus.WAITING);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void exit_withNoPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.WAITING);
        }
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void exit_withPlayersPlaying() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        }
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 8);
        player.getSpaceShip().addCrewMember(1, false, false);
        player.getSpaceShip().addCrewMember(2, false, false);
        state.play(player);
        assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @Test
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 8);
        player.getSpaceShip().addCrewMember(1, false, false);
        player.getSpaceShip().addCrewMember(2, false, false);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }
}