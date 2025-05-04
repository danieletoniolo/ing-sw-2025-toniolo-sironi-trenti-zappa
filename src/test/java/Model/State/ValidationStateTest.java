package Model.State;

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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ValidationStateTest {
    ValidationState state;

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

        state = new ValidationState(board);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void setFragmentChoice_withValidFragmentChoice() {
        state.setInternalState(ValidationInternalState.FRAGMENTED_SHIP);
        state.setFragmentedComponents(new ArrayList<>());
        state.getFragmentedComponents().add(new ArrayList<>());

        assertDoesNotThrow(() -> state.setFragmentChoice(0));
        assertEquals(0, state.getFragmentChoice());
    }

    @RepeatedTest(5)
    void setFragmentChoice_withInvalidState() {
        state.setInternalState(ValidationInternalState.DEFAULT);
        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(0));
    }

    @RepeatedTest(5)
    void setComponentToDestroy_withValidComponents() {
        state.setInternalState(ValidationInternalState.DEFAULT);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<Pair<Integer, Integer>> components = new ArrayList<>();
        components.add(new Pair<>(0, 0));
        components.add(new Pair<>(1, 1));

        assertDoesNotThrow(() -> state.setComponentToDestroy(player, components));
        assertEquals(components, state.getComponentsToDestroy());
    }

    @RepeatedTest(5)
    void setComponentToDestroy_withInvalidState() {
        state.setInternalState(ValidationInternalState.FRAGMENTED_SHIP);
        PlayerData player = state.getPlayers().getFirst();
        ArrayList<org.javatuples.Pair<Integer, Integer>> components = new ArrayList<>();
        components.add(new Pair<>(0, 0));

        assertThrows(IllegalStateException.class, () -> state.setComponentToDestroy(player, components));
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingInvalidComponents() {
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component c = new Connectors(2, connector);
        state.getPlayers().forEach(p -> p.getSpaceShip().placeComponent(c, 6,7));
        state.getPlayers().forEach(p -> p.getSpaceShip().getInvalidComponents());

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(p ->
                assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(p.getColor()))
        );
    }

    @RepeatedTest(5)
    void entry_withNoPlayersHavingInvalidComponents() {
        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertNotEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()))
        );
    }

    @RepeatedTest(5)
    void entry_withEmptyPlayerList() {
        state.getPlayers().clear();

        assertDoesNotThrow(() -> state.entry());
        assertTrue(state.getInvalidComponents().isEmpty());
    }

    @RepeatedTest(5)
    void execute_withValidComponentsToDestroy_removesComponentsAndUpdatesState() {
        PlayerData player = state.getPlayers().getFirst();
        Storage s1 = new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        s1.addGood(new Good(GoodType.GREEN));
        Storage s2 = new Storage(3, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        s2.addGood(new Good(GoodType.BLUE));
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(s2, 6, 8);
        state.setInternalState(ValidationInternalState.DEFAULT);
        ArrayList<Pair<Integer, Integer>> components = new ArrayList<>();
        components.add(new Pair<>(6, 8));
        components.add(new Pair<>(6, 7));
        state.getInvalidComponents().put(player, new ArrayList<>(components));
        state.setComponentToDestroy(player, components);

        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(state.getInvalidComponents().get(player).isEmpty());
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withFragmentedShipAndValidFragmentChoice() {
        PlayerData player = state.getPlayers().getFirst();
        Storage s1 = new Storage(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        s1.addGood(new Good(GoodType.GREEN));
        Storage s2 = new Storage(3, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        s2.addGood(new Good(GoodType.BLUE));
        player.getSpaceShip().placeComponent(s1, 6, 7);
        player.getSpaceShip().placeComponent(s2, 6, 8);
        state.setInternalState(ValidationInternalState.FRAGMENTED_SHIP);
        ArrayList<Pair<Integer, Integer>> fragment = new ArrayList<>();
        fragment.add(new Pair<>(6, 8));
        fragment.add(new Pair<>(6, 7));
        state.setFragmentedComponents(new ArrayList<>());
        state.getFragmentedComponents().add(fragment);
        state.setFragmentChoice(0);

        assertDoesNotThrow(() -> state.execute(player));
        assertNull(state.getFragmentedComponents());
        assertEquals(-1, state.getFragmentChoice());
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withUnsetComponentsToDestroy() {
        PlayerData player = state.getPlayers().getFirst();
        state.setInternalState(ValidationInternalState.DEFAULT);

        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withUnsetFragmentChoice() {
        PlayerData player = state.getPlayers().getFirst();
        state.setInternalState(ValidationInternalState.FRAGMENTED_SHIP);

        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withOutOfBoundsFragmentChoice() {
        PlayerData player = state.getPlayers().getFirst();
        state.setInternalState(ValidationInternalState.FRAGMENTED_SHIP);
        state.setFragmentedComponents(new ArrayList<>());
        state.getFragmentedComponents().add(new ArrayList<>());
        state.setFragmentChoice(1);

        assertThrows(IndexOutOfBoundsException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_setsInternalStateToFragmentedShip_whenShipIsFragmented() {
        PlayerData player = state.getPlayers().getFirst();
        SpaceShip ship = player.getSpaceShip();
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component c = new Connectors(2, connector);
        Component c2 = new Connectors(3, connector);
        Component c3 = new Connectors(4, connector);
        Component c4 = new Connectors(5, connector);
        Component c5 = new Connectors(6, connector);
        ship.placeComponent(c, 6, 7);
        ship.placeComponent(c2, 6, 8);
        ship.placeComponent(c3, 6, 9);
        ship.placeComponent(c4, 7, 6);
        ship.placeComponent(c5, 5, 7);

        ArrayList<Pair<Integer, Integer>> fragment1 = new ArrayList<>();
        fragment1.add(new Pair<>(6, 8));
        fragment1.add(new Pair<>(6, 9));

        ArrayList<Pair<Integer, Integer>> fragment2 = new ArrayList<>();
        fragment2.add(new Pair<>(8, 7));
        fragment2.add(new Pair<>(7, 7));

        ArrayList<Pair<Integer, Integer>> fragment3 = new ArrayList<>();
        fragment2.add(new Pair<>(5, 7));

        state.setInternalState(ValidationInternalState.DEFAULT);
        state.getInvalidComponents().put(player, new ArrayList<>(fragment1));
        state.setComponentToDestroy(player, new ArrayList<>(fragment1));

        ship.destroyComponent(6, 7);
        ship.getDisconnectedComponents().add(fragment1);
        ship.getDisconnectedComponents().add(fragment2);
        ship.getDisconnectedComponents().add(fragment3);

        state.execute(player);

        assertEquals(ValidationInternalState.FRAGMENTED_SHIP, state.getInternalState());
    }

    @RepeatedTest(5)
    void exit_withAllPlayersHavingValidComponents() {
        state.getPlayers().forEach(player -> player.getSpaceShip().getInvalidComponents().clear());
        state.getPlayers().forEach(player -> state.playersStatus.replace(player.getColor(), PlayerStatus.PLAYED));

        assertDoesNotThrow(() -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withPlayerHavingInvalidComponents() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY};
        Component c = new Connectors(2, connector);
        player.getSpaceShip().placeComponent(c, 6, 7);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_withEmptyPlayerList() {
        state.getPlayers().clear();

        assertDoesNotThrow(() -> state.exit());
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