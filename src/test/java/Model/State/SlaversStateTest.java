package Model.State;

import Model.Cards.Slavers;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlaversStateTest {
    SlaversState state;

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

        Slavers c1 = new Slavers(2, 3, 4, 5, 2, 7);

        state = new SlaversState(board, c1);
        assertNotNull(state);
    }

    @Test
    void useCannon_invalidState(){
        state.setInternalState(SlaversInternalState.PENALTY);
        PlayerData player = state.getPlayers().getFirst();
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(IllegalStateException.class, () -> state.useCannon(player, 5.0f, batteriesID));
    }

    @RepeatedTest(5)
    void useCannon_withValidBatteriesAndPositiveStrength() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 7);
        player.getSpaceShip().placeComponent(new Battery(5, connectors, 3), 7, 8);

        assertDoesNotThrow(() -> state.useCannon(player, 5.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(5.0f, state.getStats().get(player));
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
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        assertDoesNotThrow(() -> state.useCannon(player, 0.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(0.0f, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void useCannon_withNullPlayer() {
        List<Integer> batteriesID = Arrays.asList(1, 2, 3);

        assertThrows(NullPointerException.class, () -> state.useCannon(null, 5.0f, batteriesID));
    }

    @Test
    void useCannon_valueEqualToCardRequirement() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        assertDoesNotThrow(() -> state.useCannon(player, (float) state.getCard().getCannonStrengthRequired(), player.getSpaceShip().getBatteries().keySet().stream().toList()));
        state.execute(player);
        assertNull(state.isSlaversDefeat());
    }

    @Test
    void useCannon_valueGreaterThanCardRequirement() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        assertDoesNotThrow(() -> state.useCannon(player, (float) state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        state.setAcceptCredits(false);
        state.execute(player);
        assertTrue(state.isSlaversDefeat());
    }

    @Test
    void useCannon_valueLessThanCardRequirement() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        assertDoesNotThrow(() -> state.useCannon(player, (float) state.getCard().getCannonStrengthRequired() - 1f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertFalse(state.isSlaversDefeat());
    }

    @Test
    void setCrewLoss_validCrewLoss() {
        state.setInternalState(SlaversInternalState.PENALTY);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(new Pair<>(0, 2));
        crewLoss.add(new Pair<>(1, 2));
        state.setCrewLoss(crewLoss);
        assertEquals(crewLoss, state.getCrewLoss());
    }

    @Test
    void setCrewLoss_invalidState() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(new Pair<>(0, 2));
        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_crewRemovedNotEqualToCardRequirement() {
        state.setInternalState(SlaversInternalState.PENALTY);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(new Pair<>(0, 1));
        crewLoss.add(new Pair<>(1, 1));
        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setCrewLoss_emptyCrewLossList() {
        state.setInternalState(SlaversInternalState.PENALTY);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(crewLoss));
    }

    @Test
    void setAcceptCredits_validState() {
        state.setInternalState(SlaversInternalState.PENALTY);
        state.setAcceptCredits(true);
        assertTrue(state.getAcceptCredits());
    }

    @Test
    void setAcceptCredits_invalidState() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        assertThrows(IllegalStateException.class, () -> state.setAcceptCredits(true));
    }

    @Test
    void isSlaversDefeat_returnsTrueWhenSlaversDefeated() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setAcceptCredits(false);
        state.execute(player);
        assertTrue(state.isSlaversDefeat());
    }

    @Test
    void isSlaversDefeat_returnsFalseWhenSlaversNotDefeated() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() - 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        assertFalse(state.isSlaversDefeat());
    }

    @Test
    void isSlaversDefeat_returnsNullWhenCannonStrengthEqualsRequirement() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Cannon(3, connectors, 1), 6, 8);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 7, 6);

        state.useCannon(player, (float) state.getCard().getCannonStrengthRequired(), player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.execute(player);
        assertNull(state.isSlaversDefeat());
    }

    @Test
    void entry_allPlayersHaveCannonsStrength() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            assertEquals(player.getSpaceShip().getSingleCannonsStrength(), state.getStats().get(player));
        }
    }

    @Test
    void entry_playersWithPurpleAlien_addsAlienStrengthToStats() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.getPlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportPurple(2, connectors), 7, 6)
        );
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, false, true);
        state.players.get(1).getSpaceShip().getCabin(154).isValid();
        state.players.get(1).getSpaceShip().addCrewMember(154, false, true);
        state.players.get(2).getSpaceShip().getCabin(153).isValid();
        state.players.get(2).getSpaceShip().addCrewMember(153, false, true);
        state.players.get(3).getSpaceShip().getCabin(155).isValid();
        state.players.get(3).getSpaceShip().addCrewMember(155, false, true);
        float alienStrength = SpaceShip.getAlienStrength();

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertEquals(alienStrength, state.getStats().get(player))
        );
    }

    @Test
    void execute_slaversDefeatedAndAcceptCredits() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setInternalState(SlaversInternalState.PENALTY);
        state.setAcceptCredits(true);
        int initialCoins = player.getCoins();
        int initialSteps = player.getStep();
        state.execute(player);
        assertEquals(initialCoins + state.getCard().getCredit(), player.getCoins());
        assertTrue(initialSteps - state.getCard().getFlightDays() > player.getStep());
    }

    @Test
    void execute_slaversDefeatedAndDeclineCredits() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setInternalState(SlaversInternalState.PENALTY);
        state.setAcceptCredits(false);
        int initialCoins = player.getCoins();
        int initialSteps = player.getStep();
        state.execute(player);
        assertEquals(initialCoins, player.getCoins());
        assertEquals(initialSteps, player.getStep());
    }

    @Test
    void execute_slaversNotDefeatedAndCrewLossSet_removesCrewMembersCorrectly() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() - 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setInternalState(SlaversInternalState.PENALTY);
        player.getSpaceShip().destroyComponent(6, 7);
        player.getSpaceShip().destroyComponent(7, 6);
        Cabin c1 = new Cabin(2, connectors);
        Cabin c2 = new Cabin(3, connectors);
        player.getSpaceShip().addCrewMember(152, false, false);
        player.getSpaceShip().placeComponent(c1, 6, 7);
        player.getSpaceShip().placeComponent(c2, 7, 6);
        player.getSpaceShip().addCrewMember(2, false, false);
        player.getSpaceShip().addCrewMember(3, false, false);
        ArrayList<Pair<Integer, Integer>> crewLoss = new ArrayList<>();
        crewLoss.add(new Pair<>(c1.getID(), 2));
        crewLoss.add(new Pair<>(c2.getID(), 2));
        state.setCrewLoss(crewLoss);
        int initialCrew = player.getSpaceShip().getCrewNumber();
        state.execute(player);
        assertEquals(initialCrew - state.getCard().getCrewLost(), player.getSpaceShip().getCrewNumber());
    }

    @Test
    void execute_slaversNotDefeatedAndCrewLossNotSet() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() - 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setInternalState(SlaversInternalState.PENALTY);
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_slaversDefeatedAndAcceptCreditsNotSet() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 7, 6);

        state.useCannon(player, state.getCard().getCannonStrengthRequired() + 1f, player.getSpaceShip().getBatteries().keySet().stream().toList());
        state.setInternalState(SlaversInternalState.PENALTY);
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_throwsExceptionWhenCannonsNotSet() {
        state.setInternalState(SlaversInternalState.SET_CANNONS);
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(player));
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