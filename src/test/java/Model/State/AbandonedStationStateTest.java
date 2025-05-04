package Model.State;

import Model.Cards.AbandonedStation;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationStateTest {
    AbandonedStationState state;

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
        PlayerData p0 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        state = new AbandonedStationState(board, c1);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void addCannonStrength_correctly_or_withNegativeStrength() {
        UUID uuid = state.getPlayers().getFirst().getUUID();
        state.getCannonStrength().put(uuid, 5.0f);
        state.addCannonStrength(uuid, 3.0f);
        assertEquals(8.0f, state.getCannonStrength().get(uuid));

        UUID uuid1 = state.getPlayers().getFirst().getUUID();
        state.getCannonStrength().put(uuid1, 5.0f);
        state.addCannonStrength(uuid1, -2.0f);
        assertEquals(3.0f, state.getCannonStrength().get(uuid1));
    }

    @Test
    void addCannonStrength_withNonExistingUUID() {
        UUID nonExistingUUID = UUID.randomUUID();
        assertThrows(NullPointerException.class, () -> state.addCannonStrength(nonExistingUUID, 3.0f));
    }

    @RepeatedTest(5)
    void setGoodsToExchange_correctly_or_withEmptyExchangeData() {
        ArrayList<Good> goodsToGive = new ArrayList<>(List.of(new Good(GoodType.YELLOW), new Good(GoodType.BLUE)));
        ArrayList<Good> goodsToReceive = new ArrayList<>(List.of(new Good(GoodType.GREEN)));
        Triplet<ArrayList<Good>, ArrayList<Good>, Integer> exchange = new Triplet<>(goodsToGive, goodsToReceive, 1);
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>(List.of(exchange));
        PlayerData player = state.getPlayers().getFirst();
        state.setGoodsToExchange(player, exchangeData);
        assertEquals(exchangeData, state.getExchangeData());

        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> emptyExchangeData = new ArrayList<>();
        PlayerData player1 = state.getPlayers().getFirst();
        state.setGoodsToExchange(player1, emptyExchangeData);
        assertTrue(state.getExchangeData().isEmpty());
    }

    @RepeatedTest(5)
    void entry_initializesCannonStrengthForAllPlayers() {
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            assertNotNull(state.getCannonStrength().get(player.getUUID()));
            assertEquals(player.getSpaceShip().getSingleCannonsStrength(), state.getCannonStrength().get(player.getUUID()));
        }
    }

    @RepeatedTest(5)
    void entry_doesNotThrowExceptionWhenCalledMultipleTimes() {
        assertDoesNotThrow(() -> {
            state.entry();
            state.entry();
        });
    }

    @Test
    void execute_updatesPlayerStatusToPlayedWhenPlayerIsPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        state.execute(player);

        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_performsGoodsExchange() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        ArrayList<Good> g1 = new ArrayList<>(List.of(new Good(GoodType.YELLOW)));
        player.getSpaceShip().placeComponent(s, 6, 7);
        player.getSpaceShip().exchangeGood(g1, null, 2);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        ArrayList<Good> goodsToReceive = new ArrayList<>(List.of(new Good(GoodType.BLUE)));
        Triplet<ArrayList<Good>, ArrayList<Good>, Integer> exchange = new Triplet<>(goodsToReceive, g1, 2);
        state.setGoodsToExchange(player, new ArrayList<>(List.of(exchange)));
        state.execute(player);

        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(goodsToReceive, player.getSpaceShip().getStorage(2).getGoods());
    }

    @RepeatedTest(5)
    void execute_withWaitingPlayer() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        state.execute(player);

        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @RepeatedTest(5)
    void execute_withNoExchangeData() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);

        state.setGoodsToExchange(player, new ArrayList<>());

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertEquals(6 - state.getCard().getFlightDays(), state.getPlayers().get(0).getStep());
        assertEquals(3, state.getPlayers().get(1).getStep());
        assertEquals(1, state.getPlayers().get(2).getStep());
        assertEquals(0, state.getPlayers().get(3).getStep());
    }

    @RepeatedTest(5)
    void exit_withWaitingOrPlayingPlayer() {
        state.playersStatus.put(state.getPlayers().get(0).getColor(), PlayerStatus.PLAYED);
        state.playersStatus.put(state.getPlayers().get(1).getColor(), PlayerStatus.WAITING);

        assertDoesNotThrow(() -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withNoPlayersPlayed() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        }
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withPlayersPlaying() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        }
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void getPlayerPosition() {
        PlayerData player = state.getPlayers().getFirst();
        int position = state.getPlayerPosition(player);
        assertEquals(0, position);
    }

    @RepeatedTest(5)
    void getPlayerPosition_withPlayerNotInList_or_withNullPlayer() {
        PlayerData nonExistentPlayer = new PlayerData("123e4567-e89b-12d3-a456-426614174006", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        assertThrows(IllegalArgumentException.class, () -> state.getPlayerPosition(nonExistentPlayer));

        assertThrows(IllegalArgumentException.class, () -> state.getPlayerPosition(null));
    }

    @RepeatedTest(5)
    void haveAllPlayersPlayed_withAllPlayersPlayed_or_whenAllPlayersSkipped() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertTrue(state.haveAllPlayersPlayed());

        state.setStatusPlayers(PlayerStatus.SKIPPED);
        assertTrue(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void haveAllPlayersPlayed_whenSomePlayersHaveNotPlayed_or_whenMixedStatuses() {
        state.setStatusPlayers(PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());

        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.getPlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void setStatusPlayers() {
        state.setStatusPlayers(PlayerStatus.PLAYING);
        for (PlayerData player : state.getPlayers()) {
            assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
        }
    }

    @RepeatedTest(5)
    void getCurrentPlayer_returnsFirstWaitingPlayer() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.getPlayers().get(1).getColor(), PlayerStatus.WAITING);
        state.playersStatus.put(state.getPlayers().get(2).getColor(), PlayerStatus.WAITING);
        PlayerData currentPlayer = state.getCurrentPlayer();
        assertEquals(state.getPlayers().get(1), currentPlayer);
    }

    @RepeatedTest(5)
    void getCurrentPlayer_whenAllPlayersHavePlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.getCurrentPlayer());
    }

    @RepeatedTest(5)
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void play_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @RepeatedTest(5)
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void entry_doesNotThrowException() {
        assertDoesNotThrow(() -> state.entry());
    }
}