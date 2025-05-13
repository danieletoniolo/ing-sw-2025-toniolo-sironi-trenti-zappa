package Model.State;

import Model.Cards.OpenSpace;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceStateTest {
    OpenSpaceState state;

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

        OpenSpace c1 = new OpenSpace(2, 1);

        state = new OpenSpaceState(board, c1);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void useEngine_withValidBatteriesAndPositiveStrength() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(4, connectors, 3), 6, 8);
        player.getSpaceShip().placeComponent(new Battery(5, connectors, 3), 6, 9);

        assertDoesNotThrow(() -> state.useEngine(player, 5.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(5.0f, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void useEngine_withInvalidBatteryIDs() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        List<Integer> invalidBatteriesID = Arrays.asList(99, 100);
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);

        assertThrows(NullPointerException.class, () -> state.useEngine(player, 5.0f, invalidBatteriesID));
    }

    @RepeatedTest(5)
    void useEngine_withZeroStrength() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6);
        player.getSpaceShip().placeComponent(new Battery(3, connectors, 3), 6, 7);

        assertDoesNotThrow(() -> state.useEngine(player, 0.0f, player.getSpaceShip().getBatteries().keySet().stream().toList()));
        assertEquals(0.0f, state.getStats().get(player));
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingSingleEngines() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new Engine(2, connectors, 1), 7, 6)
        );

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                assertEquals(player.getSpaceShip().getSingleEnginesStrength(), state.getStats().get(player))
        );
    }

    @RepeatedTest(5)
    void entry_withPlayersHavingBrownAlien() {
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        state.board.getInGamePlayers().forEach(player ->
                player.getSpaceShip().placeComponent(new LifeSupportBrown(2, connectors), 7, 6)
        );
        state.players.getFirst().getSpaceShip().getCabin(152).isValid();
        state.players.getFirst().getSpaceShip().addCrewMember(152, 1);
        state.players.get(1).getSpaceShip().getCabin(154).isValid();
        state.players.get(1).getSpaceShip().addCrewMember(154, 1);
        state.players.get(2).getSpaceShip().getCabin(153).isValid();
        state.players.get(2).getSpaceShip().addCrewMember(153, 1);
        state.players.get(3).getSpaceShip().getCabin(155).isValid();
        state.players.get(3).getSpaceShip().addCrewMember(155, 1);
        float alienStrength = SpaceShip.getAlienStrength();

        assertDoesNotThrow(() -> state.entry());
        state.board.getInGamePlayers().forEach(player ->
                assertEquals(alienStrength, state.getStats().get(player))
        );
    }

    @RepeatedTest(5)
    void execute_withPositiveStats() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.getStats().put(player, 5.0f);
        //int initialSteps = state.board.getStepsOfAPlayer(player);

        assertDoesNotThrow(() -> state.execute(player));
        //assertEquals(initialSteps + 5, state.board.getStepsOfAPlayer(player));
        assertFalse(player.hasGivenUp());
    }

    @RepeatedTest(5)
    void execute_withZeroStats() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.getStats().put(player, 0.0f);

        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(player.hasGivenUp());
    }

    @RepeatedTest(5)
    void execute_withNullStats() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.getStats().put(player, null);

        assertThrows(NullPointerException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withPlayerNotInStats() {
        PlayerData player = new PlayerData("123e4567-e89b-12d3-a456-426614174005", PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));

        assertThrows(NullPointerException.class, () -> state.execute(player));
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
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertFalse(state.haveAllPlayersPlayed());
    }

    @RepeatedTest(5)
    void setStatusPlayers() {
        state.setStatusPlayers(PlayerStatus.PLAYING);
        for (PlayerData player : state.board.getInGamePlayers()) {
            assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
        }
    }

    @RepeatedTest(5)
    void getCurrentPlayer_returnsFirstWaitingPlayer() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.board.getInGamePlayers().get(1).getColor(), PlayerStatus.WAITING);
        state.playersStatus.put(state.board.getInGamePlayers().get(2).getColor(), PlayerStatus.WAITING);
        PlayerData currentPlayer = state.getCurrentPlayer();
        assertEquals(state.board.getInGamePlayers().get(1), currentPlayer);
    }

    @RepeatedTest(5)
    void getCurrentPlayer_whenAllPlayersHavePlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.getCurrentPlayer());
    }

    @RepeatedTest(5)
    void play_updatesPlayerStatusToPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void play_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @RepeatedTest(5)
    void play_withPlayerAlreadyPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        state.play(player);
        assertEquals(PlayerStatus.PLAYING, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @RepeatedTest(5)
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);

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