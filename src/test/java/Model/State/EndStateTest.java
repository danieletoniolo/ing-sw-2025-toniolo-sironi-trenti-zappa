package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Good.Good;
import Model.Good.GoodType;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.ConnectorType;
import Model.SpaceShip.SpaceShip;
import Model.SpaceShip.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EndStateTest {
    EndState state;

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

        state = new EndState(board, Level.SECOND);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void entry_initializesScoresWithPlayerCoins() {
        state.entry();
        for (PlayerData player : state.getPlayers()) {
            assertEquals(player.getCoins(), state.getScores().get(player));
        }
    }

    @RepeatedTest(5)
    void entry_withEmptyPlayerList() throws JsonProcessingException {
        Board b1 = new Board(Level.SECOND);
        EndState emptyState = new EndState(b1, Level.SECOND);
        assertDoesNotThrow(emptyState::entry);
        assertTrue(emptyState.getScores().isEmpty());
    }

    @RepeatedTest(5)
    void execute_withAllPlayersPlayed() {
        state.entry();
        for(PlayerData p : state.getPlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.FINISH_ORDER);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(EndInternalState.BEST_LOOKING_SHIP, state.getEndInternalState());
    }

    @RepeatedTest(5)
    void execute_withPlayerGivingUp_updatesScoresCorrectly() {
        PlayerData player = state.getPlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        player.getSpaceShip().placeComponent(s, 6,7);
        player.getSpaceShip().getStorage(2).addGood(new Good(GoodType.GREEN));
        player.getSpaceShip().getStorage(2).addGood(new Good(GoodType.YELLOW));

        state.entry();
        player.setGaveUp(true);
        for(PlayerData p : state.getPlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.SALE_OF_GOODS);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(Math.round((float) player.getSpaceShip().getGoodsValue() / 2), state.getScores().get(player));
    }

    @RepeatedTest(5)
    void execute_withBestLookingShip() {
        state.entry();
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().refreshExposedConnectors();

        for(PlayerData p : state.getPlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.BEST_LOOKING_SHIP);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(player.getCoins() + 2 * state.getLevel().getValue(), state.getScores().get(player));
    }

    @RepeatedTest(5)
    void execute_withUnknownEndInternalState() {
        PlayerData player = state.getPlayers().getFirst();

        int i = 0;
        for(PlayerData p : state.getPlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(null);

            if(i == state.getPlayers().size() - 1) {
                assertThrows(NullPointerException.class, () -> state.execute(p));
            } else {
                assertDoesNotThrow(() -> state.execute(p));
            }
            i++;
        }
    }

    @RepeatedTest(5)
    void execute_withComponentLosses() {
        state.entry();
        PlayerData player = state.getPlayers().getFirst();
        Storage s = new Storage(2, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2);
        player.getSpaceShip().getLostComponents().add(s);
        int lostComponents = player.getSpaceShip().getLostComponents().size();
        int reservedComponents = player.getSpaceShip().getReservedComponents().size();

        for(PlayerData p : state.getPlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.LOSSES);

            assertDoesNotThrow(() -> state.execute(p));
        }
        System.out.println(state.getScores().get(player));
        assertEquals(player.getCoins() - (lostComponents + reservedComponents), state.getScores().get(player));
    }

    //----------
    //Test di state

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