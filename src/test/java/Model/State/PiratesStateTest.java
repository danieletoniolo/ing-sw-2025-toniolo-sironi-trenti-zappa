package Model.State;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.Hit;
import Model.Cards.Hits.HitType;
import Model.Cards.Pirates;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesStateTest {
    PiratesState state;

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

        List<Hit> fires = List.of(new Hit(HitType.HEAVYFIRE, Direction.NORTH), new Hit(HitType.LIGHTFIRE, Direction.SOUTH), new Hit(HitType.SMALLMETEOR, Direction.EAST));
        Pirates c1 = new Pirates(2, 1, fires, 2, 4, 3);

        state = new PiratesState(board, c1);
        assertNotNull(state);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setAcceptCredits_valid(boolean acceptCredits) {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        state.setAcceptCredits(acceptCredits);
        assertEquals(acceptCredits, state.acceptCredits);
    }

    @Test
    void setAcceptCredits_invalid() {
        assertThrows(IllegalStateException.class, () -> state.setAcceptCredits(true));
    }

    @Test
    void setFragmentChoice_validFragmentChoice() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        state.fightHandler.setInternalState(FightHandlerInternalState.DESTROY_FRAGMENT);
        assertDoesNotThrow(() -> state.setFragmentChoice(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 6, Integer.MAX_VALUE})
    void setFragmentChoice_invalidFragmentChoice(int invalidFragmentChoice) {
        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(invalidFragmentChoice));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void setProtect_validStateAndBattery(boolean protect) {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        state.fightHandler.setInternalState(FightHandlerInternalState.PROTECTION);
        assertDoesNotThrow(() -> state.setProtect(protect, 1));
    }

    @Test
    void setProtect_invalidState() {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        assertThrows(IllegalStateException.class, () -> state.setProtect(true, 1));
    }

    @Test
    void setProtect_nullBatteryID() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        state.fightHandler.setInternalState(FightHandlerInternalState.PROTECTION);
        assertThrows(IllegalArgumentException.class, () -> state.setProtect(true, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 6})
    void setDice_validDiceValue(int validDiceValue) {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        state.fightHandler.setInternalState(FightHandlerInternalState.CAN_PROTECT);
        assertDoesNotThrow(() -> state.setDice(validDiceValue));
    }

    @Test
    void setDice_invalidState() {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        assertThrows(IllegalStateException.class, () -> state.setDice(3));
    }

    @Test
    void useCannon_invalidState(){
        PlayerData player = state.getPlayers().getFirst();
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(IllegalStateException.class, () -> state.useCannon(player, 5.0f, batteriesID));
    }

    @RepeatedTest(5)
    void useCannon_withValidBatteriesAndPositiveStrength() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 8, 7);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 8, 8);
        player.getSpaceShip().placeComponent(new Battery(5, connectors, 3), 8, 9);

        assertDoesNotThrow(() -> state.useCannon(player, 5.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(5.0f, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void useCannon_withInvalidBatteryIDs() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        List<Integer> invalidBatteriesID = Arrays.asList(99, 100);
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, invalidBatteriesID));
    }

    @RepeatedTest(5)
    void useCannon_withNullBatteriesList() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);

        assertThrows(NullPointerException.class, () -> state.useCannon(player, 5.0f, null));
    }

    @RepeatedTest(5)
    void useCannon_withZeroStrength() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 8, 7);

        assertDoesNotThrow(() -> state.useCannon(player, 0.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(0.0f, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void useCannon_withNullPlayer() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(NullPointerException.class, () -> state.useCannon(null, 5.0f, batteriesID));
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingSingleCannon() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.getPlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7)
        );

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertEquals(player.getSpaceShip().getSingleCannonsStrength(), state.getStats().get(player))
        );
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingPurpleAlien() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.getPlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportPurple(2, connectors), 6, 7)
        );
        state.getPlayers().forEach(player ->
                player.getSpaceShip().getCabin(1).isValid()
        );
        state.getPlayers().forEach(player ->
                player.getSpaceShip().addCrewMember(1, false, true)
        );
        float alienStrength = SpaceShip.getAlienStrength();

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertEquals(alienStrength, state.getStats().get(player))
        );
    }

    @Test
    void execute_defaultStateWithHigherStats() {
        state.setInternalStatePirates(PiratesInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, state.getCard().getCannonStrengthRequired() + 1f);
        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(state.piratesDefeat);
    }

    @Test
    void execute_defaultStateWithLowerStats() {
        state.setInternalStatePirates(PiratesInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, state.getCard().getCannonStrengthRequired() - 1f);
        assertDoesNotThrow(() -> state.execute(player));
        assertFalse(state.piratesDefeat);
    }

    @Test
    void execute_defaultStateWithEqualStats() {
        state.setInternalStatePirates(PiratesInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, (float) state.getCard().getCannonStrengthRequired());
        assertDoesNotThrow(() -> state.execute(player));
        assertNull(state.piratesDefeat);
    }

    @Test
    void execute_middleStateWithPiratesDefeatTrueAndAcceptCredits_addsCoinsAndReducesFlightDays() {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        state.piratesDefeat = true;
        state.acceptCredits = true;
        PlayerData player = state.getPlayers().getFirst();
        int initialCoins = player.getCoins();
        int initialSteps = player.getStep();
        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(initialCoins + state.getCard().getCredit(), player.getCoins());
        assertTrue(initialSteps - state.getCard().getFlightDays() > player.getStep());
    }

    @Test
    void execute_middleStateWithPiratesDefeatAndNullAcceptCredits() {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        state.piratesDefeat = true;
        state.acceptCredits = null;
        PlayerData player = state.getPlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_middleStateWithPiratesDefeatFalse_addsPlayerToDefeatedList() {
        state.setInternalStatePirates(PiratesInternalState.MIDDLE);
        state.piratesDefeat = false;
        PlayerData player = state.getPlayers().getFirst();
        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(state.playersDefeated.contains(player));
    }

    @Test
    void execute_penaltyStateWithDefeatedPlayer_executesFightHandler() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        state.playersDefeated.add(player);
        state.fightHandler.setHitIndex(0);
        state.fightHandler.setDice(7);
        assertDoesNotThrow(() -> state.execute(player));
    }

    @Test
    void execute_penaltyStateWithNonDefeatedPlayer() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_penaltyStateWithOutOfBoundsHitIndex() {
        state.setInternalStatePirates(PiratesInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        state.playersDefeated.add(player);
        state.fightHandler.setHitIndex(state.getCard().getFires().size());
        assertThrows(IndexOutOfBoundsException.class, () -> state.execute(player));
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