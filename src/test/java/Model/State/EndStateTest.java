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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EndStateTest {
    EndState state;

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

        state = new EndState(board, Level.SECOND);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void entry_initializesScoresWithPlayerCoins() {
        state.entry();
        for (PlayerData player : state.board.getInGamePlayers()) {
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

    //TODO: Da testare
    @Test
    void execute(){
        PlayerData player1 = state.players.getFirst();
        PlayerData player2 = state.players.get(1);
        PlayerData player3 = state.players.get(2);
        PlayerData player4 = state.players.get(3);

        ConnectorType[] connectors = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        player1.getSpaceShip().placeComponent(s, 6,7);
        player1.getSpaceShip().getStorage(2).addGood(new Good(GoodType.GREEN));
        player1.getSpaceShip().getStorage(2).addGood(new Good(GoodType.YELLOW));

        Storage s1 = new Storage(3, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2);
        player1.getSpaceShip().getLostComponents().add(s1);
        int lostComponents = player1.getSpaceShip().getLostComponents().size();
        int reservedComponents = player1.getSpaceShip().getReservedComponents().size();

        state.entry();

        int initialScore1 = state.getScores().get(player1);
        int initialScore2 = state.getScores().get(player2);
        int initialScore3 = state.getScores().get(player3);
        int initialScore4 = state.getScores().get(player4);
        state.execute(player4);
        assertEquals(EndInternalState.BEST_LOOKING_SHIP, state.getEndInternalState());
        System.out.println(state.getScores().get(player1));
        assertEquals(initialScore1 + 8, state.getScores().get(player1)); //Level Second
        assertEquals(initialScore2 + 6, state.getScores().get(player2));
        assertEquals(initialScore3 + 4, state.getScores().get(player3));
        assertEquals(initialScore4 + 2, state.getScores().get(player4));

        int initialScore = state.getScores().get(player2);
        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        assertEquals(initialScore + 2 * state.getLevel().getValue(), state.getScores().get(player2));
        assertEquals(EndInternalState.SALE_OF_GOODS, state.getEndInternalState());

        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        assertEquals(EndInternalState.LOSSES, state.getEndInternalState());
        assertEquals(Math.round((float) player1.getSpaceShip().getGoodsValue() / 2), state.getScores().get(player1));

        initialScore = state.getScores().get(player2);
        for(PlayerData p : state.board.getInGamePlayers()) {
            assertDoesNotThrow(() -> state.execute(p));
        }
        System.out.println(state.getScores().get(player2));
        assertEquals(initialScore - (lostComponents + reservedComponents), state.getScores().get(player2));
    }

    /*
    @RepeatedTest(5)
    void execute_withAllPlayersPlayed() {
        state.entry();
        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.FINISH_ORDER);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(EndInternalState.BEST_LOOKING_SHIP, state.getEndInternalState());
    }

    @RepeatedTest(5)
    void execute_withPlayerGivingUp_updatesScoresCorrectly() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Storage s = new Storage(2, connectors, true, 2);
        player.getSpaceShip().placeComponent(s, 6,7);
        player.getSpaceShip().getStorage(2).addGood(new Good(GoodType.GREEN));
        player.getSpaceShip().getStorage(2).addGood(new Good(GoodType.YELLOW));

        state.entry();
        player.setGaveUp(true);
        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.SALE_OF_GOODS);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(Math.round((float) player.getSpaceShip().getGoodsValue() / 2), state.getScores().get(player));
    }

    @RepeatedTest(5)
    void execute_withBestLookingShip() {
        state.entry();
        PlayerData player = state.board.getInGamePlayers().getFirst();

        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.BEST_LOOKING_SHIP);

            assertDoesNotThrow(() -> state.execute(p));
        }

        assertEquals(player.getCoins() + 2 * state.getLevel().getValue(), state.getScores().get(player));
    }

    @RepeatedTest(5)
    void execute_withUnknownEndInternalState() {
        int i = 0;
        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(null);

            if(i == state.board.getInGamePlayers().size() - 1) {
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
        PlayerData player = state.board.getInGamePlayers().getFirst();
        Storage s = new Storage(2, new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE}, true, 2);
        player.getSpaceShip().getLostComponents().add(s);
        int lostComponents = player.getSpaceShip().getLostComponents().size();
        int reservedComponents = player.getSpaceShip().getReservedComponents().size();

        for(PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.replace(p.getColor(), PlayerStatus.PLAYED);
            state.setEndInternalState(EndInternalState.LOSSES);

            assertDoesNotThrow(() -> state.execute(p));
        }
        System.out.println(state.getScores().get(player));
        assertEquals(player.getCoins() - (lostComponents + reservedComponents), state.getScores().get(player));
    }

     */

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