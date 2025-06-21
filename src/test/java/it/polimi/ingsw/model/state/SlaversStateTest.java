package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Slavers;
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

class SlaversStateTest {
    Field statsField = SlaversState.class.getDeclaredField("cannonStrength");
    Field cardField = SlaversState.class.getDeclaredField("card");
    Field internalStateField = SlaversState.class.getDeclaredField("internalState");
    Field slaversDefeatField = SlaversState.class.getDeclaredField("slaversDefeat");

    SlaversState state;
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

    SlaversStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        statsField.setAccessible(true);
        cardField.setAccessible(true);
        internalStateField.setAccessible(true);
        slaversDefeatField.setAccessible(true);

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

        Slavers c1 = new Slavers(2, 3, 1, 5, 2, 1);

        state = new SlaversState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @RepeatedTest(3)
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
        internalStateField.set(state, SlaversState.SlaversInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, null, 3), 6, 8);
        List<Integer> ids = List.of(2);
        List<Integer> batteries = List.of(3);

        assertDoesNotThrow(() -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @Test
    void useExtraStrength_withTypeOneAndInvalidState() throws IllegalAccessException {
        internalStateField.set(state, SlaversState.SlaversInternalState.PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);

        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @Test
    void setPenaltyLoss_withTypeZero() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 0, cabinsID));
    }

    @Test
    void setPenaltyLoss_withTypeOne() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 1, cabinsID));
    }

    @Test
    void setPenaltyLoss_withTypeTwo() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().addCrewMember(1, false, false);
        List<Integer> cabinsID = List.of(1);

        assertDoesNotThrow(() -> state.setPenaltyLoss(player, 2, cabinsID));
    }

    @Test
    void setPenaltyLoss_withInvalidType() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(player, 99, cabinsID));
    }

    @Test
    void entry_allPlayersHaveCannonsStrength() throws IllegalAccessException {
        state.entry();
        for (PlayerData player : state.board.getInGamePlayers()) {
            assertEquals(player.getSpaceShip().getSingleCannonsStrength(), ((Map<PlayerData, Float>) statsField.get(state)).get(player));
        }
    }

    @Test
    void entry_playersWithPurpleAlien_addsAlienStrengthToStats() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
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
        state.board.getInGamePlayers().forEach(player ->
                {
                    try {
                        assertEquals(alienStrength, ((Map<PlayerData, Float>) statsField.get(state)).get(player));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Test
    void execute_slaversDefeatedAndAcceptCredits() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        internalStateField.set(state, SlaversState.SlaversInternalState.ENEMY_DEFEAT);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 7);
        player.getSpaceShip().addCrewMember(152, false, false);
        player.getSpaceShip().addCrewMember(2, false, false);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);

        state.entry();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 5.0f);
        state.execute(player);
        assertEquals(SlaversState.SlaversInternalState.REWARD, internalStateField.get(state));
        assertTrue((boolean) slaversDefeatField.get(state));

        int initialStep = player.getStep();
        state.execute(player);
        assertEquals(5, player.getCoins());
        assertTrue(initialStep - ((Slavers) cardField.get(state)).getFlightDays() >= player.getStep());
    }

    @Test
    void execute_slaversNotDefeated() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        internalStateField.set(state, SlaversState.SlaversInternalState.ENEMY_DEFEAT);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 7);
        player.getSpaceShip().addCrewMember(152, false, false);
        player.getSpaceShip().addCrewMember(2, false, false);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);

        state.entry();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 1.0f);
        state.execute(player);
        assertEquals(SlaversState.SlaversInternalState.PENALTY, internalStateField.get(state));
        assertFalse((boolean) slaversDefeatField.get(state));
    }

    @Test
    void execute_slaversNotDefeatedAndNoCrewLoss() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        internalStateField.set(state, SlaversState.SlaversInternalState.ENEMY_DEFEAT);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);

        state.entry();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, 1.0f);
        state.execute(player);
        assertEquals(SlaversState.SlaversInternalState.GIVE_UP, internalStateField.get(state));
        assertFalse((boolean) slaversDefeatField.get(state));
    }

    @Test
    void execute_throwsExceptionWhenCannonsNotSet() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(NullPointerException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @RepeatedTest(5)
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);

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