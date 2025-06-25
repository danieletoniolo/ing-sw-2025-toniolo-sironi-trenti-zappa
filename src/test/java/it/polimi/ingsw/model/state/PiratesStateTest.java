package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.*;
import it.polimi.ingsw.model.cards.Pirates;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PiratesStateTest {
    Field internalStateField = PiratesState.class.getDeclaredField("internalState");
    Field fragmentsField = PiratesState.class.getDeclaredField("fragments");
    Field statsField = PiratesState.class.getDeclaredField("cannonsStrength");
    Field cardField = PiratesState.class.getDeclaredField("card");
    Field piratesDefeatField = PiratesState.class.getDeclaredField("piratesDefeat");
    Field playersDefeatedField = PiratesState.class.getDeclaredField("playersDefeated");
    Field hitIndexField = PiratesState.class.getDeclaredField("hitIndex");

    PiratesState state;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th = _ -> {};

    PiratesStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        internalStateField.setAccessible(true);
        fragmentsField.setAccessible(true);
        statsField.setAccessible(true);
        cardField.setAccessible(true);
        piratesDefeatField.setAccessible(true);
        hitIndexField.setAccessible(true);
        playersDefeatedField.setAccessible(true);

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

        List<Hit> fires = List.of(new Hit(HitType.HEAVYFIRE, Direction.NORTH), new Hit(HitType.LIGHTFIRE, Direction.SOUTH), new Hit(HitType.SMALLMETEOR, Direction.EAST));
        Pirates c1 = new Pirates(2, 1, fires, 2, 4, 3);

        state = new PiratesState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @Test
    void setFragmentChoice_clearsFragmentsAfterValidChoice() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 4);

        List<Pair<Integer, Integer>> fragment1 = new ArrayList<>(List.of(new Pair<>(6, 7), new Pair<>(6, 8)));
        List<Pair<Integer, Integer>> fragment2 = new ArrayList<>(List.of(new Pair<>(6, 5), new Pair<>(6, 4)));
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).addAll(List.of(fragment1, fragment2));
        assertDoesNotThrow(() -> state.setFragmentChoice(player, 0));
        assertTrue(((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).isEmpty());
    }

    @Test
    void setFragmentChoice_whenNoFragmentsAvailable() throws IllegalAccessException {
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).clear();
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(player, 0));
    }

    @Test
    void setFragmentChoice_whenInvalidFragmentChoice() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).add(List.of(Pair.with(1, 1), Pair.with(2, 2)));
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(NullPointerException.class, () -> state.setFragmentChoice(player, 2));
    }

    @Test
    void setProtect() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.CAN_PROTECT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] c = new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        player.getSpaceShip().placeComponent(new Battery(1, c, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(2, c), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(2, c), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(2, c), 6, 4);
        player.getSpaceShip().placeComponent(new Connectors(2, c), 6, 9);
        player.getSpaceShip().placeComponent(new Connectors(2, c), 6, 3);
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).add(List.of(Pair.with(6, 7)));
        state.rollDice(player);
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        state.execute(player);
        assertDoesNotThrow(() -> state.setProtect(player, 1));
        state.setFragmentChoice(player, 0);
        assertTrue(((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).isEmpty());
    }

    @Test
    void setProtect_whenDiceNotRolled() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.setProtect(player, 1));
    }

    @Test
    void rollDice_triggersEventsForValidPlayer() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.CAN_PROTECT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 4);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 9);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 3);

        assertDoesNotThrow(() -> state.rollDice(player));
    }

    @Test
    void rollDice_wrongState() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.rollDice(player));
    }

    @Test
    void rollDice_whenAlreadyRolled() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.rollDice(player));
    }

    @RepeatedTest(3)
    void useExtraStrength_typeZero_or_invalidType() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);
        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 0, ids, batteries));
        assertThrows(IllegalArgumentException.class, () -> state.useExtraStrength(player, 3, ids, batteries));
    }

    @RepeatedTest(3)
    void useExtraStrength_typeOne() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> ids = List.of(1, 2);
        List<Integer> batteries = List.of(3, 4);

        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @RepeatedTest(3)
    void useExtraStrength_typeOne_inEnemyDefeat() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Cannon(2, new ConnectorType[]{ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE}, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 8);
        List<Integer> ids = List.of(2);
        List<Integer> batteries = List.of(1);

        assertDoesNotThrow(() -> state.useExtraStrength(player, 1, ids, batteries));
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingSingleCannon() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new Cannon(2, connectors, 1), 6, 7)
        );

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                {
                    try {
                        assertEquals(player.getSpaceShip().getSingleCannonsStrength(), ((Map<PlayerData, Float>) statsField.get(state)).get(player));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingPurpleAlien() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportPurple(2, connectors), 6, 7)
        );
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, false, true);
        state.players.get(1).getSpaceShip().getCabin(154).isValid();
        state.players.get(1).getSpaceShip().addCrewMember(154, false, true);
        state.players.get(2).getSpaceShip().getCabin(153).isValid();
        state.players.get(2).getSpaceShip().addCrewMember(153, false, true);
        state.players.get(3).getSpaceShip().getCabin(155).isValid();
        state.players.get(3).getSpaceShip().addCrewMember(155, false, true);
        float alienStrength = state.players.getFirst().getSpaceShip().getAlienStrength(false);

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
    void execute_stateWithHigherStats() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, ((Pirates) cardField.get(state)).getCannonStrengthRequired() + 1f);
        assertDoesNotThrow(() -> state.execute(player));
        assertTrue((Boolean) piratesDefeatField.get(state));
    }

    @Test
    void execute_stateWithLowerStats() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, ((Pirates) cardField.get(state)).getCannonStrengthRequired() - 1f);
        assertDoesNotThrow(() -> state.execute(player));
        assertFalse((Boolean) piratesDefeatField.get(state));
    }

    @Test
    void execute_stateWithEqualStats() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.ENEMY_DEFEAT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, Float>) statsField.get(state)).put(player, (float) ((Pirates) cardField.get(state)).getCannonStrengthRequired());
        assertDoesNotThrow(() -> state.execute(player));
        assertNull((Boolean) piratesDefeatField.get(state));
    }

    @Test
    void execute_withPiratesDefeatTrue_addsCoinsAndReducesFlightDays() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.REWARD);
        piratesDefeatField.set(state, true);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYING);
        int initialCoins = player.getCoins();
        int initialSteps = player.getStep();
        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(initialCoins + ((Pirates) cardField.get(state)).getCredit(), player.getCoins());
        assertTrue(initialSteps - ((Pirates) cardField.get(state)).getFlightDays() > player.getStep());
    }

    @Test
    void execute_penaltyStateWithDefeatedPlayer() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.CAN_PROTECT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.rollDice(player);
        internalStateField.set(state, PiratesState.PiratesInternalState.PENALTY);
        ((ArrayList<PlayerData>) playersDefeatedField.get(state)).add(player);
        assertDoesNotThrow(() -> state.execute(player));
    }

    @Test
    void execute_penaltyTransition_setsPenaltyStateAndResetsDiceRolled() throws IllegalAccessException {
        internalStateField.set(state, PiratesState.PiratesInternalState.REWARD);
        PlayerData lastPlayer = state.board.getInGamePlayers().getLast();
        state.playersStatus.put(lastPlayer.getColor(), State.PlayerStatus.SKIPPED);
        ArrayList<PlayerData> playersDefeated = (ArrayList<PlayerData>) playersDefeatedField.get(state);
        playersDefeated.add(lastPlayer);
        playersDefeatedField.set(state, playersDefeated);
        assertDoesNotThrow(() -> state.execute(lastPlayer));
        assertEquals(PiratesState.PiratesInternalState.CAN_PROTECT, internalStateField.get(state));
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