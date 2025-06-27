package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.model.state.exception.SynchronousStateException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.fixed.FieldAlignment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValidationStateTest {
    Field invalidComponentsField = ValidationState.class.getDeclaredField("invalidComponents");
    Field fragmentedComponentsField = ValidationState.class.getDeclaredField("fragmentedComponents");
    ValidationState state;
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
    StateTransitionHandler th;

    ValidationStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        fragmentedComponentsField.setAccessible(true);
        invalidComponentsField.setAccessible(true);

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

        state = new ValidationState(board, ecb, th);
        assertNotNull(state);
    }

    @Test
    void getCurrentPlayer() {
        assertThrows(SynchronousStateException.class, () -> state.getCurrentPlayer());
    }

    @Test
    void setFragmentChoice_withValidFragmentChoice() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 4);
        player.getSpaceShip().destroyComponent(6, 6);
        ArrayList<Pair<Integer, Integer>> fragment1 = new ArrayList<>(List.of(new Pair<>(6, 7), new Pair<>(6, 8)));
        ArrayList<Pair<Integer, Integer>> fragment2 = new ArrayList<>(List.of(new Pair<>(6, 5), new Pair<>(6, 4)));
        ((Map<PlayerData, List<List<Pair<Integer, Integer>>>>) fragmentedComponentsField.get(state)).put(player, List.of(fragment1, fragment2));

        assertDoesNotThrow(() -> state.setFragmentChoice(player, 0));
        assertNull(((Map<PlayerData, List<List<Pair<Integer, Integer>>>>) fragmentedComponentsField.get(state)).get(player));
    }

    @Test
    void setFragmentChoice_withOutOfBoundsFragmentChoice() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ArrayList<Pair<Integer, Integer>> fragment1 = new ArrayList<>(List.of(new Pair<>(6, 7), new Pair<>(6, 8)));
        ((Map<PlayerData, List<List<Pair<Integer, Integer>>>>) fragmentedComponentsField.get(state)).put(player, List.of(fragment1));

        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(player, 2));
        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(player, -1));
    }

    @Test
    void setFragmentChoice_withNullFragmentedComponents() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ((Map<PlayerData, List<List<Pair<Integer, Integer>>>>) fragmentedComponentsField.get(state)).put(player, null);

        assertThrows(NullPointerException.class, () -> state.setFragmentChoice(player, 0));
    }

    @Test
    void placeMarker_wrongBoard() throws JsonProcessingException {
        Board b = new Board(Level.SECOND);
        State L = new ValidationState(b, ecb, th);
        PlayerData player = new PlayerData("p0", UUID.randomUUID().toString(), PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));
        L.board.addInGamePlayers(player);
        assertThrows(IllegalStateException.class, () -> L.placeMarker(player, 1));
    }

    @Test
    void placeMarker() throws JsonProcessingException {
        Board b = new Board(Level.LEARNING);
        State L = new ValidationState(b, ecb, th);
        PlayerData player = new PlayerData("p0", UUID.randomUUID().toString(), PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));
        L.board.addInGamePlayers(player);
        assertEquals(0, L.board.getInGamePlayers().getFirst().getPosition());
    }



    @Test
    void setComponentToDestroy_withValidComponents() throws IllegalAccessException, NoSuchFieldException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ArrayList<Pair<Integer, Integer>> components = new ArrayList<>();
        components.add(new Pair<>(6, 7));
        components.add(new Pair<>(6, 8));
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);

        Field invalidComponentsField = ValidationState.class.getDeclaredField("invalidComponents");
        invalidComponentsField.setAccessible(true);
        ((Map<PlayerData, ArrayList<Pair<Integer, Integer>>>) invalidComponentsField.get(state)).put(player, components);

        assertDoesNotThrow(() -> state.setComponentToDestroy(player, components));
        assertTrue(player.getSpaceShip().getInvalidComponents().isEmpty());
    }

    @Test
    void setComponentToDestroy_withNonExistentComponents() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ArrayList<Pair<Integer, Integer>> components = new ArrayList<>();
        components.add(new Pair<>(10, 10));

        assertThrows(NullPointerException.class, () -> state.setComponentToDestroy(player, components));
    }

    @Test
    void setComponentToDestroy_withEmptyComponentsList() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ArrayList<Pair<Integer, Integer>> components = new ArrayList<>();

        assertDoesNotThrow(() -> state.setComponentToDestroy(player, components));
        assertTrue(player.getSpaceShip().getInvalidComponents().isEmpty());
    }

    @Test
    void entry_withPlayersHavingInvalidComponents() {
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component c = new Connectors(2, connector);
        state.board.getInGamePlayers().forEach(p -> p.getSpaceShip().placeComponent(c, 6,7));
        state.board.getInGamePlayers().forEach(p -> p.getSpaceShip().getInvalidComponents());

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(p ->
                assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(p.getColor()))
        );
    }

    @Test
    void entry_withNoPlayersHavingInvalidComponents() {
        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                assertEquals(State.PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()))
        );
    }

    @Test
    void entry_withEmptyPlayerList() throws IllegalAccessException {
        state.board.getInGamePlayers().clear();

        assertDoesNotThrow(() -> state.entry());
        ((Map<PlayerData, ArrayList<Pair<Integer, Integer>>>) invalidComponentsField.get(state)).values().forEach(
                a -> assertEquals(0, a.size())
        );
    }

    @Test
    void execute_withValidPlayerAndNoInvalidComponents() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().getInvalidComponents().clear();

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withValidPlayerAndInvalidComponents() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component c = new Connectors(2, connector);
        player.getSpaceShip().placeComponent(c, 6, 7);

        assertDoesNotThrow(() -> state.execute(player));
        assertFalse(((Map<PlayerData, ArrayList<Pair<Integer, Integer>>>) invalidComponentsField.get(state)).get(player).isEmpty());
    }

    @Test
    void execute_withFragmentedShip() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        SpaceShip ship = player.getSpaceShip();
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component c1 = new Connectors(2, connector);
        Component c2 = new Connectors(3, connector);
        ship.placeComponent(c1, 6, 7);
        ship.placeComponent(c2, 6, 8);
        ship.destroyComponent(6, 7);

        assertDoesNotThrow(() -> state.execute(player));
    }

    @Test
    void exit_withAllPlayersHavingValidComponents() {
        state.board.getInGamePlayers().forEach(player -> player.getSpaceShip().getInvalidComponents().clear());
        state.board.getInGamePlayers().forEach(player -> state.playersStatus.replace(player.getColor(), State.PlayerStatus.PLAYED));

        assertDoesNotThrow(() -> state.exit());
    }

    @Test
    void exit_withPlayerHavingInvalidComponents() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component c = new Connectors(2, connector);
        player.getSpaceShip().placeComponent(c, 6, 7);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }
}