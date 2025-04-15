package Model.State;

import Model.Cards.OpenSpace;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.ConnectorType;
import Model.SpaceShip.Engine;
import Model.SpaceShip.LifeSupportBrown;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceStateTest {
    OpenSpaceState state;

    //TODO: Da testare
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

        OpenSpace c1 = new OpenSpace(2, 1);

        state = new OpenSpaceState(board, c1);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void useEngine_withPositiveStrength() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Engine e1 = new Engine(2, connectors, 1);
        Engine e2 = new Engine(3, connectors, 2);
        player.getSpaceShip().placeComponent(e1, 8, 7);
        player.getSpaceShip().placeComponent(e2, 8, 8);
        float strength_max = (float) player.getSpaceShip().getSingleEnginesStrength() + (float) player.getSpaceShip().getDoubleEnginesStrength() / 2;

        assertDoesNotThrow(() -> state.useEngine(player, strength_max));
        assertEquals(strength_max, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void useEngine_withZeroStrength() {
        state.entry();
        PlayerData player = state.getPlayers().getFirst();
        float initialStrength = state.getStats().get(player);

        assertDoesNotThrow(() -> state.useEngine(player, 0.0f));
        assertEquals(initialStrength, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingSingleEngines() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.getPlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 8, 7)
        );

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertEquals(player.getSpaceShip().getSingleEnginesStrength(), state.getStats().get(player))
        );
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingBrownAlien() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.getPlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportBrown(2, connectors), 8, 7)
        );
        state.getPlayers().forEach(player ->
                player.getSpaceShip().getCabin(1).isValid()
        );
        state.getPlayers().forEach(player ->
                player.getSpaceShip().addCrewMember(1, true, false)
        );
        float alienStrength = SpaceShip.getAlienStrength();

        assertDoesNotThrow(() -> state.entry());
        state.getPlayers().forEach(player ->
                assertEquals(alienStrength, state.getStats().get(player))
        );
    }

    @RepeatedTest(5)
    void execute_withPositiveStats() {
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, 5.0f);
        int initialSteps = state.board.getStepsOfAPlayer(player);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(initialSteps + 5, state.board.getStepsOfAPlayer(player));
        assertFalse(player.hasGivenUp());
    }

    @RepeatedTest(5)
    void execute_withZeroStats() {
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, 0.0f);

        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(player.hasGivenUp());
    }

    @RepeatedTest(5)
    void execute_withNullStats() {
        PlayerData player = state.getPlayers().getFirst();
        state.getStats().put(player, null);

        assertThrows(NullPointerException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withPlayerNotInStats() {
        PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174005", PlayerColor.BLUE, new SpaceShip(Level.SECOND, new boolean[12][12]));

        assertThrows(NullPointerException.class, () -> state.execute(player));
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