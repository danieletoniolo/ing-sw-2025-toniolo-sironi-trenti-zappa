package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.*;
import it.polimi.ingsw.model.cards.MeteorSwarm;
import it.polimi.ingsw.model.game.board.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.Battery;
import it.polimi.ingsw.model.spaceship.ConnectorType;
import it.polimi.ingsw.model.spaceship.Connectors;
import it.polimi.ingsw.model.spaceship.SpaceShip;
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

class MeteorSwarmStateTest {
    Field fragmentsField = MeteorSwarmState.class.getDeclaredField("fragments");
    Field rolledField = MeteorSwarmState.class.getDeclaredField("diceRolled");
    Field hitIndexField = MeteorSwarmState.class.getDeclaredField("hitIndex");
    MeteorSwarmState state;
    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th;

    MeteorSwarmStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        fragmentsField.setAccessible(true);
        rolledField.setAccessible(true);
        hitIndexField.setAccessible(true);

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

        List<Hit> meteors = List.of((new Hit(HitType.LIGHTFIRE, Direction.NORTH)), (new Hit(HitType.SMALLMETEOR, Direction.SOUTH)), (new Hit(HitType.LARGEMETEOR, Direction.NORTH)), (new Hit(HitType.HEAVYFIRE, Direction.NORTH)));
        MeteorSwarm c1 = new MeteorSwarm(2, 1, meteors);

        state = new MeteorSwarmState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @Test
    void setFragmentChoice_clearsFragmentsAfterValidChoice() throws IllegalAccessException {
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
    void setProtect() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] c = {ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Battery(1, c, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, c), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, c), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, c), 6, 4);
        player.getSpaceShip().placeComponent(new Connectors(1, c), 6, 9);
        player.getSpaceShip().placeComponent(new Connectors(1, c), 6, 3);
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).add(List.of(Pair.with(6, 7)));
        state.rollDice(player);
        assertDoesNotThrow(() -> state.setProtect(player, 1));
        assertFalse(((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).isEmpty());
    }

    @Test
    void setProtect_whenDiceNotRolled() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.setProtect(player, 1));
    }

    @Test
    void rollDice_triggersEventsForValidPlayer() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 4);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 9);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 3);

        assertDoesNotThrow(() -> state.rollDice(player));
        assertTrue((boolean) rolledField.get(state));
    }

    @Test
    void rollDice_whenAlreadyRolled() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.rollDice(player);
        assertThrows(IllegalStateException.class, () -> state.rollDice(player));
    }

    @Test
    void execute_withValidPlayer_advancesHitIndexAndResetsDiceRolled() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().get(3);
        int initialHitIndex = (int) hitIndexField.get(state);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.WAITING);

        assertDoesNotThrow(() -> state.execute(player));
        int newHitIndex = (int) hitIndexField.get(state);
        assertEquals(initialHitIndex + 1, newHitIndex);
        assertFalse((boolean) rolledField.get(state));
    }

    @Test
    void execute_withNotLastPlayer_doesNotAdvanceHitIndex() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int initialHitIndex = (int) hitIndexField.get(state);
        state.playersStatus.put(player.getColor(), State.PlayerStatus.WAITING);

        assertDoesNotThrow(() -> state.execute(player));
        int newHitIndex = (int) hitIndexField.get(state);
        assertEquals(initialHitIndex, newHitIndex);
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