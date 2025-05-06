package Model.State;

import Model.Cards.Smugglers;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersStateTest {
    SmugglersState state;

    @BeforeEach
    void setUp() throws JsonProcessingException {
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

        List<Good> good = List.of(new Good(GoodType.GREEN), new Good(GoodType.YELLOW));
        Smugglers c1 = new Smugglers(2, 1, good, 3, 4, 5);

        state = new SmugglersState(board, c1);
        assertNotNull(state);
    }

    @Test
    void useCannon_invalidState(){
        state.setInternalState(SmugglerInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(IllegalStateException.class, () -> state.useCannon(player, 5.0f, batteriesID));
    }

    @RepeatedTest(5)
    void useCannon_withValidBatteriesAndPositiveStrength() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        player.getSpaceShip().placeComponent(new Battery(5, connectors, 3), 7, 8);

        state.entry();
        assertDoesNotThrow(() -> state.useCannon(player, 5.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(5.0f, state.getCannonStrength().get(player));
    }

    @RepeatedTest(5)
    void useCannon_withInvalidBatteryIDs() {
        PlayerData player = state.getPlayers().getFirst();
        List<Integer> invalidBatteriesID = Arrays.asList(99, 100);
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, invalidBatteriesID));
    }

    @RepeatedTest(5)
    void useCannon_withNullBatteriesList() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, null));
    }

    @RepeatedTest(5)
    void useCannon_withZeroStrength() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.entry();
        assertDoesNotThrow(() -> state.useCannon(player, 0.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(0.0f, state.getCannonStrength().get(player));
    }

    @RepeatedTest(5)
    void useCannon_withNullPlayer() {
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(NullPointerException.class, () -> state.useCannon(null, 5.0f, batteriesID));
    }

    @Test
    void setGoodsToExchange_validExchangeData() {
        state.setInternalState(SmugglerInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>();
        exchangeData.add(new Triplet<>(new ArrayList<>(), new ArrayList<>(), 1));
        state.setGoodsToExchange(player, exchangeData);
        assertEquals(exchangeData, state.getExchangeData());
    }

    @Test
    void setGoodsToExchange_invalidState() {
        state.setInternalState(SmugglerInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void setGoodsToExchange_nullExchangeData() {
        state.setInternalState(SmugglerInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        state.setGoodsToExchange(player, null);
        assertNull(state.getExchangeData());
    }

    @Test
    void setGoodsToDiscard_validGoodsToDiscard() {
        state.setInternalState(SmugglerInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(new Pair<>(new ArrayList<>(), 1));
        state.setGoodsToDiscard(player, goodsToDiscard);
        assertEquals(goodsToDiscard, state.getGoodsToDiscard());
    }

    @Test
    void setGoodsToDiscard_invalidState() {
        state.setInternalState(SmugglerInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> state.setGoodsToDiscard(player, goodsToDiscard));
    }

    @Test
    void setGoodsToDiscard_nullGoodsToDiscard() {
        state.setInternalState(SmugglerInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        state.setGoodsToDiscard(player, null);
        assertNull(state.getGoodsToDiscard());
    }

    @Test
    void setCrewToLose_validCrewToLose() {
        state.setInternalState(SmugglerInternalState.PENALTY);
        ArrayList<Pair<Integer, Integer>> crewToLose = new ArrayList<>();
        crewToLose.add(new Pair<>(1, 2));
        state.setCrewToLose(crewToLose);
        assertEquals(crewToLose, state.getCrewToLose());
    }

    @Test
    void setCrewToLose_invalidState() {
        state.setInternalState(SmugglerInternalState.DEFAULT);
        ArrayList<Pair<Integer, Integer>> crewToLose = new ArrayList<>();
        crewToLose.add(new Pair<>(1, 2));
        assertThrows(IllegalStateException.class, () -> state.setCrewToLose(crewToLose));
    }

    @Test
    void setCrewToLose_nullCrewToLose() {
        state.setInternalState(SmugglerInternalState.PENALTY);
        state.setCrewToLose(null);
        assertNull(state.getCrewToLose());
    }

    @Test
    void entry_initializesCannonStrengthForAllPlayers() {
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            assertEquals(player.getSpaceShip().getSingleCannonsStrength(), state.getCannonStrength().get(player));
        }
    }

    @Test
    void execute_validPlayerWithSufficientCannonStrength() {
        state.entry();
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s1 = new Storage(2, connectors, true, 2);
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        ArrayList<Good> add = new ArrayList<>();
        add.add(new Good(GoodType.GREEN));
        ArrayList<Good> remove = new ArrayList<>();
        player.getSpaceShip().exchangeGood(add, remove, 2);
        state.useCannon(player, 10.0f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.execute(player);
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_validPlayerWithExactCannonStrength_marksPlayerAsSkipped() {
        state.entry();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, state.getCard().getCannonStrengthRequired() - player.getSpaceShip().getSingleCannonsStrength(), player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.execute(player);
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_validPlayerWithInsufficientCannonStrength() {
        state.entry();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, -10.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()); // Ensure insufficient strength
        state.execute(player);
        assertEquals(SmugglerInternalState.PENALTY, state.getInternalState());
    }

    @Test
    void execute_nullPlayer_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @Test
    void execute_penaltyStateWithoutGoodsOrCrew() {
        state.entry();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, -10.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()); // Ensure penalty state
        state.execute(player);
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_penaltyStateWithGoods() {
        state.entry();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, -10.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()); // Ensure penalty state
        Storage s1 = new Storage(2, connectors, true, 2);
        player.getSpaceShip().placeComponent(s1, 6, 7);
        ArrayList<Good> add = new ArrayList<>();
        add.add(new Good(GoodType.GREEN));
        ArrayList<Good> remove = new ArrayList<>();
        player.getSpaceShip().exchangeGood(add, remove, 2);
        state.execute(player);

        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(new Pair<>(new ArrayList<>(List.of(new Good(GoodType.GREEN))), 2));
        state.setGoodsToDiscard(player, goodsToDiscard);

        state.execute(player);
        assertNull(state.getGoodsToDiscard());
        assertEquals(SmugglerInternalState.DEFAULT, state.getInternalState());
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_penaltyStateWithCrew_discardsCrewAndResetsState() {
        state.entry();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        state.useCannon(player, -10.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()); // Ensure penalty state
        Storage s1 = new Storage(2, connectors, true, 2);
        Cabin c2 = new Cabin(2, connectors);
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(c2, 6, 8);
        ArrayList<Good> add = new ArrayList<>();
        add.add(new Good(GoodType.GREEN));
        ArrayList<Good> remove = new ArrayList<>();
        player.getSpaceShip().exchangeGood(add, remove, 2);
        player.getSpaceShip().addCrewMember(2, false, false);
        state.execute(player);

        ArrayList<Pair<Integer, Integer>> crewToLose = new ArrayList<>();
        crewToLose.add(new Pair<>(c2.getID(), 2));
        state.setCrewToLose(crewToLose);

        state.execute(player);
        assertNull(state.getCrewToLose());
        assertEquals(SmugglerInternalState.DEFAULT, state.getInternalState());
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withExchangeData() {
        state.entry();
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s1 = new Storage(2, connectors, true, 2);
        ArrayList<Good> g1 = new ArrayList<>(List.of(new Good(GoodType.YELLOW)));
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        player.getSpaceShip().exchangeGood(g1, null, 2);
        ArrayList<Good> goodsToAdd = new ArrayList<>(List.of(new Good(GoodType.GREEN)));
        ArrayList<Triplet<ArrayList<Good>, ArrayList<Good>, Integer>> exchangeData = new ArrayList<>();
        exchangeData.add(new Triplet<>(goodsToAdd, g1, s1.getID()));
        state.setGoodsToExchange(player, exchangeData);
        state.useCannon(player, (float) state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());

        state.execute(player);

        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));

        SpaceShip ship = player.getSpaceShip();
        assertTrue(ship.getGoods().containsAll(goodsToAdd));
        assertFalse(ship.getGoods().containsAll(g1));
    }

    @Test
    void exit_allPlayersPlayed() {
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.replace(player.getColor(), PlayerStatus.PLAYED);
        }
        int initialSteps = state.getPlayers().getFirst().getStep();
        state.exit();
        for (PlayerData player : state.getPlayers()) {
            if (state.playersStatus.get(player.getColor()) == PlayerStatus.PLAYED) {
                assertTrue(initialSteps - state.getCard().getFlightDays() >= player.getStep());
            }
        }
    }

    @Test
    void exit_noPlayersPlayed_doesNotReduceFlightDays() {
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.replace(player.getColor(), PlayerStatus.SKIPPED);
        }
        int[] initialSteps = new int[state.getPlayers().size()];
        for(int i = 0; i < state.getPlayers().size(); i++) {
            initialSteps[i] = state.getPlayers().get(i).getStep();
        }
        state.exit();
        for(int i = 0; i < state.getPlayers().size(); i++) {
            assertEquals(initialSteps[i], state.getPlayers().get(i).getStep());
        }
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
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @RepeatedTest(5)
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.getPlayers().getFirst().getColor(), PlayerStatus.WAITING);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withNoPlayers() {
        state.players.clear();
        state.playersStatus.clear();

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }
}