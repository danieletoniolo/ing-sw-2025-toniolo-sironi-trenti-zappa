package Model.State;

import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BuildingStateTest {
    BuildingState state;
    Board board;
    PlayerData p0;

    //TODO: Finire

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

        state = new BuildingState(board);
    }

    //TODO: Controllare
    @RepeatedTest(5)
    void flipTimer_withLearningLevel() throws JsonProcessingException {
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        PlayerData p = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        Board b = new Board(Level.LEARNING);
        b.clearInGamePlayers();
        b.setPlayer(p, 0);
        b.refreshInGamePlayers();
        BuildingState s = new BuildingState(b);
        assertThrows(IllegalStateException.class, () -> s.flipTimer(p));
    }

    @RepeatedTest(5)
    void flipTimer_whenTimerAlreadyRunning() {
        state.flipTimer(p0);
        assertThrows(IllegalStateException.class, () -> state.flipTimer(p0));
    }

    @RepeatedTest(5)
    void flipTimer_withValidFirstFlip() {
        assertDoesNotThrow(() -> state.flipTimer(p0));
        assertTrue(state.getTimerRunning());
    }

    @RepeatedTest(2)
    void flipTimer_withThirdFlipAndPlayerNotFinishedBuilding() throws InterruptedException {
        state.flipTimer(p0);
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.flipTimer(p0);
        assertThrows(IllegalStateException.class, () -> state.flipTimer(p0));
    }

    @RepeatedTest(2)
    void flipTimer_withThirdFlipAndPlayerFinishedBuilding() throws InterruptedException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.flipTimer(player);
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.flipTimer(player);
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertDoesNotThrow(() -> state.flipTimer(player));
        assertTrue(state.getTimerRunning());
    }

    //TODO: Così funziona, ma :
    // 1) Non elimino tutti i player null
    // 2) Quando creo lo stato ho già la board, quindi il metodo mi dà sempre errore
    // 3) In questo caso non ho eliminato il player da get(1)
    @RepeatedTest(5)
    void placeMarker_withValidPosition() throws JsonProcessingException {
        Board board1 = new Board(Level.SECOND);
        BuildingState state1 = new BuildingState(board1);
        PlayerData player = new PlayerData(UUID.randomUUID().toString(), PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));
        PlayerData player1 = new PlayerData(UUID.randomUUID().toString(), PlayerColor.RED, new SpaceShip(Level.SECOND, PlayerColor.RED));
        board1.clearInGamePlayers();
        state1.board.getInGamePlayers().set(0, player);
        state1.board.getInGamePlayers().set(1, player1);
        state1.entry();

        assertDoesNotThrow(() -> state1.placeMarker(player1, 2));
        assertEquals(player1, board1.getInGamePlayers().get(2));
    }

    @RepeatedTest(5)
    void placeMarker_withInvalidPosition() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int invalidPosition = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.placeMarker(player, invalidPosition));
    }

    @RepeatedTest(5)
    void placeMarker_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        SpaceShip ship = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        PlayerData newPlayer = new PlayerData(nonExistentUUID.toString(), PlayerColor.BLUE, ship);
        int validPosition = 1;
        assertThrows(IllegalStateException.class, () -> state.placeMarker(newPlayer, validPosition));
    }

    @RepeatedTest(5)
    void placeTile_placesTileInBoardWhenToWhereIsZero() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 0, 0, 0));
        assertTrue(state.board.getTiles().contains(component));
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void placeTile_placesTileInReserveWhenToWhereIsOne() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 1, 0, 0));
        assertTrue(player.getSpaceShip().getReservedComponents().contains(component));
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void placeTile_placesTileInSpaceShipWhenToWhereIsTwo() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);

        assertDoesNotThrow(() -> state.placeTile(player, 2, 6, 7));
        assertEquals(component, player.getSpaceShip().getComponents()[6][7]);
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void placeTile_throwsExceptionWhenPlayerHasNoTileInHand() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 2, 6, 7));
    }

    @RepeatedTest(5)
    void placeTile_throwsExceptionWhenPlayerHasFinishedBuilding() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 2, 6, 7));
    }

    @RepeatedTest(5)
    void placeTile_throwsExceptionWhenToWhereIsInvalid() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);

        assertThrows(IllegalStateException.class, () -> state.placeTile(player, 3, 6, 7));
    }

    @RepeatedTest(5)
    void rotateTile_rotatesTileClockwiseWhenPlayerHasTileInHand() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);

        assertDoesNotThrow(() -> state.rotateTile(player));
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateTile_throwsExceptionWhenPlayerHasNoTileInHand() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalStateException.class, () -> state.rotateTile(player));
    }

    @RepeatedTest(5)
    void rotateTile_throwsExceptionWhenPlayerHasFinishedBuilding() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.getPlayersHandQueue().put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.rotateTile(player));
    }

    @RepeatedTest(5)
    void pickTile_picksTileFromBoardWhenValidTileIDProvided() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertDoesNotThrow(() -> state.pickTile(player, 0, 1));
    }

    @RepeatedTest(5)
    void pickTile_throwsExceptionWhenInvalidFromWhereProvided() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        int invalidFromWhere = 3;

        assertThrows(IllegalStateException.class, () -> state.pickTile(player, invalidFromWhere, 1));
    }

    @RepeatedTest(5)
    void pickTile_throwsExceptionWhenPlayerHasFinishedBuilding() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);

        assertThrows(IllegalStateException.class, () -> state.pickTile(player, 0, 1));
    }

    @RepeatedTest(5)
    void pickTile_throwsExceptionWhenTileIDDoesNotMatch() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        ConnectorType[] connectors = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        Component component = new Connectors(1, connectors);
        state.board.putTile(component);

        assertThrows(IndexOutOfBoundsException.class, () -> state.pickTile(player, 0, 999));
    }

    @RepeatedTest(5)
    void entry_initializesStateCorrectly() {
        state.entry();
        assertNotNull(state.getPlayersHandQueue());
        assertFalse(state.getTimerRunning());
        assertEquals(0, state.getNumberOfTimerFlips());
    }

    @RepeatedTest(5)
    void execute_withValidPlayer() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertDoesNotThrow(() -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @RepeatedTest(5)
    void exit_executesWithoutExceptions() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        assertDoesNotThrow(() -> state.exit());
    }

    @RepeatedTest(5)
    void exit_resetsStateCorrectly() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        state.exit();
        assertFalse(state.getTimerRunning());
        assertEquals(0, state.getNumberOfTimerFlips());
        assertTrue(state.getPlayersHandQueue().isEmpty());
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