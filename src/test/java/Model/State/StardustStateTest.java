package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.ConnectorType;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class StardustStateTest {
    StardustState state;

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

        state = new StardustState(board);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void entry_withExposedConnectors() {
        ConnectorType[] connector = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};

        PlayerData p = state.getPlayers().getFirst();
        Storage s = new Storage(2, connector, true, 2);
        p.getSpaceShip().placeComponent(s, 6,7);

        PlayerData p2 = state.getPlayers().get(2);
        Storage s2 = new Storage(3, connector, true, 2);
        Storage s3 = new Storage(4, connector, true, 2);
        p2.getSpaceShip().placeComponent(s2, 6,7);
        p2.getSpaceShip().placeComponent(s3, 6,8);

        state.entry();

        //First number represent the initial cell, the second number represent the number of other players that he passed, the third number represent the number of his own exposed connectors
        assertEquals(-3, state.getPlayers().get(0).getStep()); // 6 -3 - 6
        assertEquals(-4, state.getPlayers().get(1).getStep()); // 3 -3 - 4
        assertEquals(-10, state.getPlayers().get(2).getStep()); // 1 -3 - 8
        assertEquals(-6, state.getPlayers().get(3).getStep()); // 0 -2 - 4
    }

    @RepeatedTest(5)
    void entry_withNoExposedConnectors() {
        PlayerData player = state.getPlayers().getFirst();
        Storage s0 = new Storage(2, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY}, true, 2);
        Storage s1 = new Storage(3, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.TRIPLE}, true, 2);
        Storage s2 = new Storage(4, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);
        Storage s3 = new Storage(5, new ConnectorType[]{ ConnectorType.EMPTY, ConnectorType.TRIPLE, ConnectorType.EMPTY, ConnectorType.EMPTY}, true, 2);

        player.getSpaceShip().placeComponent(s0, 5,6);
        player.getSpaceShip().placeComponent(s1, 6,5);
        player.getSpaceShip().placeComponent(s2, 7,6);
        player.getSpaceShip().placeComponent(s3, 6,7);

        state.entry();

        assertEquals(6, state.getPlayers().getFirst().getStep());
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
    void execute_withPlayingPlayer_updatesStatusToPlayed() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        PlayerData player1 = state.getPlayers().get(1);
        state.playersStatus.put(player1.getColor(), PlayerStatus.WAITING);

        state.execute(player);
        state.execute(player1);

        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player1.getColor()));
    }

    @RepeatedTest(5)
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
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