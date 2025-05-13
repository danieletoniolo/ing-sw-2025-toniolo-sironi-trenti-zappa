package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class CrewStateTest {
    CrewState state;
    PlayerData p0;
    Board board;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        p0 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        state = new CrewState(board);
    }

    @RepeatedTest(5)
    void manageCrewMember_removesCrewWhenModeIsRemoveAndCabinExists() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().addCrewMember(152, 0);

        assertDoesNotThrow(() -> state.manageCrewMember(player, 1, 0, 152));
        assertEquals(0, player.getSpaceShip().getCrewNumber());
        assertDoesNotThrow(() -> state.manageCrewMember(player, 0, 0, 152));
        assertEquals(2, player.getSpaceShip().getCrewNumber());
    }

    @RepeatedTest(5)
    void manageCrewMember_throwsExceptionWhenCabinDoesNotExist() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalArgumentException.class, () -> state.manageCrewMember(player, 0, 0, 999));
    }

    @RepeatedTest(5)
    void manageCrewMember_throwsExceptionWhenAddingToFullCabin() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().addCrewMember(152, 0);

        assertThrows(IllegalStateException.class, () -> state.manageCrewMember(player, 0, 0, 152));
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
    void execute_withPlayingPlayer_updatesStatusToPlayed() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        PlayerData player1 = state.board.getInGamePlayers().get(1);
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

    @RepeatedTest(5)
    void entry() {
        assertDoesNotThrow(() -> state.entry());
        assertFalse(state.played);
    }

    @RepeatedTest(5)
    void execute_withPlayerInGame() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.execute(player);
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }
}