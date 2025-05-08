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

    //TODO: Finire

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
        assertThrows(IllegalStateException.class, () -> s.flipTimer(UUID.randomUUID()));
    }

    @RepeatedTest(5)
    void flipTimer_whenTimerAlreadyRunning() {
        state.flipTimer(UUID.randomUUID());
        assertThrows(IllegalStateException.class, () -> state.flipTimer(UUID.randomUUID()));
    }

    @RepeatedTest(5)
    void flipTimer_withValidFirstFlip() {
        assertDoesNotThrow(() -> state.flipTimer(UUID.randomUUID()));
        assertTrue(state.getTimerRunning());
    }

    @RepeatedTest(2)
    void flipTimer_withThirdFlipAndPlayerNotFinishedBuilding() throws InterruptedException {
        state.flipTimer(UUID.randomUUID());
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.flipTimer(UUID.randomUUID());
        assertThrows(IllegalStateException.class, () -> state.flipTimer(UUID.randomUUID()));
    }

    @RepeatedTest(2)
    void flipTimer_withThirdFlipAndPlayerFinishedBuilding() throws InterruptedException {
        PlayerData player = state.getPlayers().getFirst();
        state.flipTimer(player.getUUID());
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.flipTimer(player.getUUID());
        try{
            Thread.sleep(BuildingState.getTimerDuration() + 100);
        } catch (InterruptedException _) {
        }
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertDoesNotThrow(() -> state.flipTimer(player.getUUID()));
        assertTrue(state.getTimerRunning());
    }

    @RepeatedTest(5)
    void showDeck_withValidDeckIndex() {
        PlayerData player = state.getPlayers().getFirst();
        Connectors c = new Connectors(2, new ConnectorType[4]);
        player.getSpaceShip().placeComponent(c, 6, 7);
        int validDeckIndex = 0;
        assertDoesNotThrow(() -> state.showDeck(player.getUUID(), validDeckIndex));
    }

    @RepeatedTest(5)
    void showDeck_withInvalidDeckIndex() {
        PlayerData player = state.getPlayers().getFirst();
        Connectors c = new Connectors(2, new ConnectorType[4]);
        player.getSpaceShip().placeComponent(c, 6, 7);
        int invalidDeckIndex = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.showDeck(player.getUUID(), invalidDeckIndex));
    }

    @RepeatedTest(5)
    void showDeck_withPlayerNotPlacedAnyTile() {
        PlayerData player = state.getPlayers().getFirst();
        int validDeckIndex = 0;
        assertThrows(IllegalStateException.class, () -> state.showDeck(player.getUUID(), validDeckIndex));
    }

    @RepeatedTest(5)
    void showDeck_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        int validDeckIndex = 0;
        assertThrows(IllegalStateException.class, () -> state.showDeck(nonExistentUUID, validDeckIndex));
    }

    //TODO: Controllare che abbiano finito il metodo nel model
    @RepeatedTest(5)
    void leaveDeck_withValidDeckIndex() {
        PlayerData player = state.getPlayers().getFirst();
        Connectors c = new Connectors(2, new ConnectorType[4]);
        player.getSpaceShip().placeComponent(c, 6, 7);
        int validDeckIndex = 0;
        state.showDeck(player.getUUID(), validDeckIndex);
        assertDoesNotThrow(() -> state.leaveDeck(player.getUUID(), validDeckIndex));
    }

    /*@RepeatedTest(5)
    void leaveDeck_withInvalidDeckIndex() {
        PlayerData player = state.getPlayers().getFirst();
        Connectors c = new Connectors(2, new ConnectorType[4]);
        player.getSpaceShip().placeComponent(c, 6, 7);
        int validDeckIndex = 0;
        state.showDeck(player.getUUID(), validDeckIndex);
        int invalidDeckIndex = -1;
        assertThrows(IllegalStateException.class, () -> state.leaveDeck(player.getUUID(), invalidDeckIndex));
    }*/

    @RepeatedTest(5)
    void leaveDeck_withNonExistentPlayerUUID() {
        PlayerData player = state.getPlayers().getFirst();
        Connectors c = new Connectors(2, new ConnectorType[4]);
        player.getSpaceShip().placeComponent(c, 6, 7);
        int validDeckIndex = 0;
        state.showDeck(player.getUUID(), validDeckIndex);
        UUID nonExistentUUID = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> state.leaveDeck(nonExistentUUID, validDeckIndex));
    }
    //Fine TODO

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
        state1.getPlayers().set(0, player);
        state1.getPlayers().set(1, player1);
        state1.entry();

        assertDoesNotThrow(() -> state1.placeMarker(player1.getUUID(), 2));
        assertEquals(player1, board1.getInGamePlayers().get(2));
    }

    @RepeatedTest(5)
    void placeMarker_withInvalidPosition() {
        PlayerData player = state.getPlayers().getFirst();
        int invalidPosition = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.placeMarker(player.getUUID(), invalidPosition));
    }

    @RepeatedTest(5)
    void placeMarker_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        int validPosition = 1;
        assertThrows(IllegalStateException.class, () -> state.placeMarker(nonExistentUUID, validPosition));
    }

    @RepeatedTest(5)
    void pickTileFromBoard_withValidTileID() {
        PlayerData player = state.getPlayers().getFirst();
        int validTileID = 1;
        assertDoesNotThrow(() -> state.pickTileFromBoard(player.getUUID(), validTileID));
        assertNotNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void pickTileFromBoard_withInvalidTileID() {
        PlayerData player = state.getPlayers().getFirst();
        int invalidTileID = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.pickTileFromBoard(player.getUUID(), invalidTileID));
    }

    @RepeatedTest(5)
    void pickTileFromBoard_withPlayerFinishedBuilding() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        int validTileID = 1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromBoard(player.getUUID(), validTileID));
    }

    @RepeatedTest(5)
    void pickTileFromBoard_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        int validTileID = 1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromBoard(nonExistentUUID, validTileID));
    }

    @RepeatedTest(5)
    void pickTileFromReserve_withValidTileID() {
        PlayerData player = state.getPlayers().getFirst();
        Component reservedComponent = new Connectors(1, new ConnectorType[4]);
        player.getSpaceShip().reserveComponent(reservedComponent);
        assertDoesNotThrow(() -> state.pickTileFromReserve(player.getUUID(), reservedComponent.getID()));
        assertEquals(reservedComponent, state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void pickTileFromReserve_withInvalidTileID() {
        PlayerData player = state.getPlayers().getFirst();
        Component reservedComponent = new Connectors(1, new ConnectorType[4]);
        player.getSpaceShip().reserveComponent(reservedComponent);
        int invalidTileID = -1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromReserve(player.getUUID(), invalidTileID));
    }

    @RepeatedTest(5)
    void pickTileFromReserve_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        int validTileID = 1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromReserve(nonExistentUUID, validTileID));
    }

    @RepeatedTest(5)
    void pickTileFromReserve_withPlayerFinishedBuilding() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        Component reservedComponent = new Connectors(1, new ConnectorType[4]);
        player.getSpaceShip().reserveComponent(reservedComponent);
        assertThrows(IllegalStateException.class, () -> state.pickTileFromReserve(player.getUUID(), reservedComponent.getID()));
    }

    @RepeatedTest(5)
    void pickTileFromSpaceship_withValidTileID() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component lastPlacedComponent = new Connectors(1, c);
        player.getSpaceShip().placeComponent(lastPlacedComponent, 6, 7);
        assertDoesNotThrow(() -> state.pickTileFromSpaceShip(player.getUUID(), lastPlacedComponent.getID()));
        assertEquals(lastPlacedComponent, state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void pickTileFromSpaceship_withInvalidTileID() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component lastPlacedComponent = new Connectors(1, c);
        player.getSpaceShip().placeComponent(lastPlacedComponent, 6, 7);
        int invalidTileID = -1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromSpaceShip(player.getUUID(), invalidTileID));
    }

    @RepeatedTest(5)
    void pickTileFromSpaceship_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        int validTileID = 1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromSpaceShip(nonExistentUUID, validTileID));
    }

    @RepeatedTest(5)
    void pickTileFromSpaceship_withPlayerFinishedBuilding() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component lastPlacedComponent = new Connectors(1, c);
        player.getSpaceShip().placeComponent(lastPlacedComponent, 6, 7);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.pickTileFromSpaceShip(player.getUUID(), lastPlacedComponent.getID()));
    }

    @RepeatedTest(5)
    void pickTileFromSpaceship_withNoLastPlacedComponent() {
        PlayerData player = state.getPlayers().getFirst();
        int validTileID = 1;
        assertThrows(IllegalStateException.class, () -> state.pickTileFromSpaceShip(player.getUUID(), validTileID));
    }

    @RepeatedTest(5)
    void leaveTile_withValidTileInHand() {
        PlayerData player = state.getPlayers().getFirst();
        state.getPlayersHandQueue().put(player.getColor(), state.board.popTile(1));
        assertDoesNotThrow(() -> state.leaveTile(player.getUUID()));
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void leaveTile_withNoTileInHand() {
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.leaveTile(player.getUUID()));
    }

    @RepeatedTest(5)
    void leaveTile_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> state.leaveTile(nonExistentUUID));
    }

    @RepeatedTest(5)
    void leaveTile_withTileNotFromReserve_putItBackInBoard() {
        PlayerData player = state.getPlayers().getFirst();
        state.getPlayersHandQueue().put(player.getColor(), state.board.popTile(2));
        assertDoesNotThrow(() -> state.leaveTile(player.getUUID()));
        assertTrue(Arrays.asList(state.board.getTiles()).contains(state.board.getTiles().get(3)));
    }

    @RepeatedTest(5)
    void leaveTile_withTileFromReserve_doesNotPutTileBackInBoard() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        player.getSpaceShip().reserveComponent(component);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertDoesNotThrow(() -> state.leaveTile(player.getUUID()));
        assertFalse(Arrays.asList(state.board.getTiles()).contains(component));
    }

    @RepeatedTest(5)
    void placeTile_withValidTileInHand() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertDoesNotThrow(() -> state.placeTile(player.getUUID(), 6, 7));
        assertEquals(component, player.getSpaceShip().getComponents()[6][7]);
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void placeTile_withNoTileInHand() {
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.placeTile(player.getUUID(), 0, 0));
    }

    @RepeatedTest(5)
    void placeTile_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> state.placeTile(nonExistentUUID, 0, 0));
    }

    @RepeatedTest(5)
    void placeTile_withPlayerFinishedBuilding() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.placeTile(player.getUUID(), 6, 7));
    }

    @RepeatedTest(5)
    void placeTile_withTileFromReserve() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        player.getSpaceShip().reserveComponent(component);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertDoesNotThrow(() -> state.placeTile(player.getUUID(), 6, 7));
        assertFalse(player.getSpaceShip().getReservedComponents().contains(component));
    }

    @RepeatedTest(5)
    void reserveTile_withValidTileInHand() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertDoesNotThrow(() -> state.reserveTile(player.getUUID()));
        assertTrue(player.getSpaceShip().getReservedComponents().contains(component));
        assertNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void reserveTile_withNoTileInHand() {
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.reserveTile(player.getUUID()));
    }

    @RepeatedTest(5)
    void reserveTile_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> state.reserveTile(nonExistentUUID));
    }

    @RepeatedTest(5)
    void reserveTile_withPlayerFinishedBuilding() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.reserveTile(player.getUUID()));
    }

    @RepeatedTest(5)
    void rotateTile_withValidTileInHand() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertDoesNotThrow(() -> state.rotateTile(player.getUUID()));
        assertEquals(1, component.getClockwiseRotation());
    }

    @RepeatedTest(5)
    void rotateTile_withNoTileInHand() {
        PlayerData player = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.rotateTile(player.getUUID()));
    }

    @RepeatedTest(5)
    void rotateTile_withNonExistentPlayerUUID() {
        UUID nonExistentUUID = UUID.randomUUID();
        assertThrows(IllegalStateException.class, () -> state.rotateTile(nonExistentUUID));
    }

    @RepeatedTest(5)
    void rotateTile_withPlayerFinishedBuilding() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.rotateTile(player.getUUID()));
    }

    @RepeatedTest(5)
    void pickTile_withValidTileIDFromPile() {
        PlayerData player = state.getPlayers().getFirst();
        int validTileID = 1;
        assertDoesNotThrow(() -> state.pickTile(player, validTileID));
        assertNotNull(state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void pickTile_withValidTileIDFromBoard() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = state.board.getTiles().get(1);
        player.getSpaceShip().placeComponent(component, 6, 7);
        assertDoesNotThrow(() -> state.pickTile(player, component.getID()));
        assertEquals(component, state.getPlayersHandQueue().get(player.getColor()));
    }

    @RepeatedTest(5)
    void pickTile_withInvalidTileID() {
        PlayerData player = state.getPlayers().getFirst();
        int invalidTileID = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.pickTile(player, invalidTileID));
    }

    @RepeatedTest(5)
    void pickTile_withAlreadyPickedTile() {
        ConnectorType[] c = new ConnectorType[]{ ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE};
        PlayerData player = state.getPlayers().getFirst();
        Component component = new Connectors(1, c);
        state.getPlayersHandQueue().put(player.getColor(), component);
        assertThrows(IllegalStateException.class, () -> state.pickTile(player, component.getID()));
    }

    @RepeatedTest(5)
    void pickTile_withNonExistentTileID() {
        PlayerData player = state.getPlayers().getFirst();
        int nonExistentTileID = -1;
        assertThrows(IndexOutOfBoundsException.class, () -> state.pickTile(player, nonExistentTileID));
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
        PlayerData player = state.getPlayers().getFirst();
        assertDoesNotThrow(() -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @RepeatedTest(5)
    void exit_executesWithoutExceptions() {
        for (PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYED);
        }
        assertDoesNotThrow(() -> state.exit());
    }

    @RepeatedTest(5)
    void exit_resetsStateCorrectly() {
        for (PlayerData player : state.getPlayers()) {
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