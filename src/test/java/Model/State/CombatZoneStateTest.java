package Model.State;

import Model.Cards.CombatZone;
import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZoneStateTest {
    CombatZoneState state;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        boolean[][] vs = new boolean[12][12];
        for (boolean[] v : vs) {
            Arrays.fill(v, true);
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, vs);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, vs);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, vs);
        PlayerData p0 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        ArrayList<Hit> fires = new ArrayList<>(List.of(new Hit(HitType.HEAVYFIRE, Direction.NORTH), new Hit(HitType.LIGHTFIRE, Direction.SOUTH), new Hit(HitType.SMALLMETEOR, Direction.EAST)));
        CombatZone c1 = new CombatZone(2, 3, fires, 2, 4);

        state = new CombatZoneState(board, c1);
        assertNotNull(state);
    }

    @Test
    void setFragmentChoice_validFragmentChoice() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.setFragmentChoice(1); //TODO: Chiedere come funziona

        assertEquals(1, state.getFightHandler().getFragmentChoice());
    }

    @Test
    void setFragmentChoice_invalidState() {
        state.setInternalState(CombatZoneInternalState.CREW);

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(1));
    }

    @Test
    void setFragmentChoice_invalidFragmentChoice() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(-1));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setProtect_validProtect(boolean b) {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.setProtect(b, 1); //TODO: Chiedere come funziona

        assertTrue(state.getFightHandler().getProtect());
        assertEquals(1, state.getFightHandler().getBatteryID());
    }

    @Test
    void setProtect_invalidState() {
        state.setInternalState(CombatZoneInternalState.CREW);

        assertThrows(IllegalStateException.class, () -> state.setProtect(true, 1));
    }

    @Test
    void setProtect_nullBatteryID() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(IllegalStateException.class, () -> state.setProtect(true, null));
    }

    @Test
    void setDice_validDice() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.setDice(3); //TODO: Chiedere come funziona

        assertEquals(3, state.getFightHandler().getDice());
    }

    @Test
    void setDice_invalidState() {
        state.setInternalState(CombatZoneInternalState.CREW);

        assertThrows(IllegalStateException.class, () -> state.setDice(2));
    }

    @Test
    void setDice_negativeDiceValue() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(IllegalStateException.class, () -> state.setDice(-1));
    }

    @Test
    void setCrewLoss_validCrewLoss_setsCrewLossSuccessfully() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));
        crewLoss.add(Pair.with(1, 2));

        state.setCrewLoss(crewLoss);

        assertEquals(crewLoss, state.getCrewLoss());
    }

    @Test
    void setCrewLoss_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_crewLossNotMatchingCardLost() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_emptyCrewLoss() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();

        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void useCannon_validStateAndStrength() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        PlayerData player = state.players.getFirst();
        state.useCannon(player, 5.0f);

        assertEquals(5.0f, state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).get(player));
    }

    @Test
    void useCannon_invalidState() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();

        assertThrows(IllegalStateException.class, () -> state.useCannon(player, 5.0f));
    }

    @Test
    void useCannon_nullPlayer() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(NullPointerException.class, () -> state.useCannon(null, 5.0f));
    }

    @Test
    void useEngine_validStateAndStrength() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        state.useEngine(player, 4.5f);

        assertEquals(4.5f, state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).get(player));
    }

    @Test
    void useEngine_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        PlayerData player = state.players.getFirst();

        assertThrows(IllegalStateException.class, () -> state.useEngine(player, 3.0f));
    }

    @Test
    void useEngine_nullPlayer() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(NullPointerException.class, () -> state.useEngine(null, 2.0f));
    }

    @Test
    void setGoodsToDiscard_validGoodsToDiscard() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(new ArrayList<>(List.of(new Good(GoodType.YELLOW), new Good(GoodType.GREEN))), 1));
        state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard);

        assertEquals(goodsToDiscard, state.getGoodsToDiscard());
    }

    @Test
    void setGoodsToDiscard_invalidState() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(new ArrayList<>(List.of(new Good(GoodType.YELLOW))), 1));

        assertThrows(IllegalStateException.class, () -> state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard));
    }

    @Test
    void setGoodsToDiscard_emptyGoodsToDiscard() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        state.setGoodsToDiscard(state.players.getFirst(), goodsToDiscard);

        assertTrue(state.getGoodsToDiscard().isEmpty());
    }

    @Test
    void entry_validState() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.entry();

        for (PlayerData player : state.players) {
            assertTrue(state.getStats().get(CombatZoneInternalState.CREW.getIndex(state.getCard().getCardLevel())).containsKey(player));
            assertTrue(state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).containsKey(player));
            assertTrue(state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).containsKey(player));
        }
    }

    @Test
    void entry_validStateWithCrew() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.entry();

        PlayerData expectedMinPlayer = state.players.stream()
                .min(Comparator.comparingInt(p -> p.getSpaceShip().getCrewNumber()))
                .orElse(null);

        assertEquals(expectedMinPlayer, state.getMinPlayerCrew());
    }

    @Test
    void entry_validStateWithEngines() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        LifeSupportBrown lsb = new LifeSupportBrown(2, c);
        Engine e = new Engine(2, c, 1);
        state.players.getFirst().getSpaceShip().placeComponent(e, 8, 7);
        state.players.getFirst().getSpaceShip().placeComponent(lsb, 6, 7);
        state.players.getFirst().getSpaceShip().getCabin(1).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(1, true, false);
        state.entry();

        Float expectedStrength = state.players.getFirst().getSpaceShip().getSingleEnginesStrength() + SpaceShip.getAlienStrength();
        assertEquals(expectedStrength, state.getStats().get(CombatZoneInternalState.ENGINES.getIndex(state.getCard().getCardLevel())).get(state.players.getFirst()));
    }

    @Test
    void entry_validStateWithCannons() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        ConnectorType[] c = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        LifeSupportPurple lsb = new LifeSupportPurple(2, c);
        Cannon e = new Cannon(2, c, 1);
        state.players.getFirst().getSpaceShip().placeComponent(e, 6, 7);
        state.players.getFirst().getSpaceShip().placeComponent(lsb, 8, 7);
        state.players.getFirst().getSpaceShip().getCabin(1).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(1, false, true);
        state.entry();

        Float expectedStrength = state.players.getFirst().getSpaceShip().getSingleCannonsStrength() + SpaceShip.getAlienStrength();
        assertEquals(expectedStrength, state.getStats().get(CombatZoneInternalState.CANNONS.getIndex(state.getCard().getCardLevel())).get(state.players.getFirst()));
    }

    //TODO: Controllare metodo execute e poi rivedere i test
    @Test
    void execute_validStateCrew_executesFlightDaysAndTransitions() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.players.getFirst().getSpaceShip().addCrewMember(1, false, false);
        state.entry();
        state.execute(state.getMinPlayerCrew());

        assertEquals(CombatZoneInternalState.ENGINES, state.getInternalState());
        assertEquals(-state.getCard().getFlightDays(), state.getMinPlayerCrew().getStep());
    }

    @Test
    void execute_validStateCrewLevelTwo() {
        state.setInternalState(CombatZoneInternalState.CREW);
        state.entry();
        state.execute(state.getMinPlayerCrew());

        assertEquals(0, state.getFightHandler().getHitIndex());
    }

    @Test
    void execute_validStateEngines_executesRemoveCrewAndTransitions() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        state.entry();
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(Pair.with(0, 1));
        state.setCrewLoss(crewLoss);

        state.execute(state.getMinPlayerEngines());

        assertEquals(CombatZoneInternalState.CANNONS, state.getInternalState());
        assertEquals(state.getCard().getLost(), state.getMinPlayerEngines().getSpaceShip().getCrewNumber());
    }

    @Test
    void execute_validStateEnginesLevelTwo() {
        state.setInternalState(CombatZoneInternalState.ENGINES);
        state.entry();
        ArrayList<Pair<ArrayList<Good>, Integer>> goodsToDiscard = new ArrayList<>();
        goodsToDiscard.add(Pair.with(new ArrayList<>(List.of(new Good(GoodType.YELLOW))), 1));
        state.setGoodsToDiscard(state.getMinPlayerEngines(), goodsToDiscard);

        state.execute(state.getMinPlayerEngines());

        assertTrue(state.getGoodsToDiscard().isEmpty());
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(state.getMinPlayerEngines().getColor()));
    }

    @Test
    void execute_validStateCannons_executesHits() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.entry();

        state.execute(state.getMinPlayerCannons());

        assertEquals(1, state.getFightHandler().getHitIndex());
    }

    @Test
    void execute_validStateCannonsLevelTwo_executesFlightDaysAndTransitions() {
        state.setInternalState(CombatZoneInternalState.CANNONS);
        state.entry();

        state.execute(state.getMinPlayerCannons());

        assertEquals(CombatZoneInternalState.CREW, state.getInternalState());
        assertEquals(-state.getCard().getFlightDays(), state.getMinPlayerCannons().getStep());
    }

    @Test
    void execute_invalidStateEngines() {
        state.setInternalState(CombatZoneInternalState.ENGINES);

        assertThrows(IllegalStateException.class, () -> state.execute(state.getMinPlayerEngines()));
    }

    @Test
    void execute_invalidStateCannons() {
        state.setInternalState(CombatZoneInternalState.CANNONS);

        assertThrows(IllegalStateException.class, () -> state.execute(state.getMinPlayerCannons()));
    }

    @RepeatedTest(5)
    void getPlayerPosition() {
        PlayerData player = state.getPlayers().getFirst();
        int position = state.getPlayerPosition(player);
        assertEquals(0, position);
    }

    @RepeatedTest(5)
    void getPlayerPosition_withPlayerNotInList_or_withNullPlayer() {
        PlayerData nonExistentPlayer = new PlayerData("123e4567-e89b-12d3-a456-426614174006", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, new boolean[12][12]));
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
    void setStatusPlayers_withNullStatus_or_withEmptyPlayersList() throws JsonProcessingException {
        assertThrows(NullPointerException.class, () -> state.setStatusPlayers(null));

        Board b = new Board(Level.SECOND);
        EndState emptyState = new EndState(b, Level.SECOND);
        assertDoesNotThrow(() -> emptyState.setStatusPlayers(PlayerStatus.WAITING));
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