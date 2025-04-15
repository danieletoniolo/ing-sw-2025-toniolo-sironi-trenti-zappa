package Model.State;

import Model.Cards.AbandonedShip;
import Model.Game.Board.Board;
import Model.Game.Board.Level;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.Cabin;
import Model.SpaceShip.ConnectorType;
import Model.SpaceShip.LifeSupportPurple;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipStateTest {
    AbandonedShipState state;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        AbandonedShip c1 = new AbandonedShip(2, 3, 3, 1, 4);

        boolean[][] vs = new boolean[12][12];
        for (boolean[] v : vs) {
            Arrays.fill(v, true);
        }
        SpaceShip ship = new SpaceShip(Level.SECOND, vs);
        PlayerData p0 = new PlayerData("123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship);
        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship);
        PlayerData p2 = new PlayerData("123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship);
        PlayerData p3 = new PlayerData("123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship);

        Board board = new Board(Level.SECOND);
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        state = new AbandonedShipState(board, c1);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void setCrewLoss() {
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        cabinsID.add(new Pair<>(1, 2));
        cabinsID.add(new Pair<>(2, 1));
        state.setCrewLoss(cabinsID);
        assertEquals(cabinsID, state.getCrewLoss());
    }

    @RepeatedTest(5)
    void setCrewLoss_withNullCabinsID_or_withEmptyCabinID() {
        assertThrows(NullPointerException.class, () -> state.setCrewLoss(null));

        ArrayList<Pair<Integer, Integer>> cID = new ArrayList<>();
        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(cID));
    }

    @RepeatedTest(5)
    void setCrewLoss_withCrewRemovedNotMatchingRequirements() {
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        cabinsID.add(new Pair<>(1, 1));
        cabinsID.add(new Pair<>(2, 1));
        assertThrows(IllegalStateException.class, () -> state.setCrewLoss(cabinsID));
    }

    @Test
    void execute() {
        for(PlayerData player : state.getPlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        }
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().getCabin(1).addCrewMember();
        Cabin c1 = new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        LifeSupportPurple lsp = new LifeSupportPurple(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        player.getSpaceShip().placeComponent(c1, 6,7);
        player.getSpaceShip().placeComponent(lsp, 6,8);
        player.getSpaceShip().getCabin(2).isValid();
        player.getSpaceShip().getCabin(2).addPurpleAlien();
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        for(Cabin c : player.getSpaceShip().getCabins().values()){
            cabinsID.add(new Pair<>(c.getID(), c.getCrewNumber()));
        }
        state.setCrewLoss(cabinsID);
        state.execute(player);
        assertEquals(state.getCard().getCredit(), player.getCoins());
    }

    @Test
    void execute_withNullPlayer_or_withCrewLossNotSet_or_withPlayerNotInState() {
        assertThrows(NullPointerException.class, () -> state.execute(null));

        PlayerData p = state.getPlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(p));

        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174005", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, new boolean[12][12]));
        assertThrows(IllegalStateException.class, () -> state.execute(p1));
    }

    @Test
    void execute_withPlayerAlreadyPlayed() {
        PlayerData p = state.getPlayers().getFirst();
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.execute(p));
    }

    @Test
    void execute_whenAlreadyPlayed() {
        for (PlayerData p : state.getPlayers()) {
            state.playersStatus.put(p.getColor(), PlayerStatus.PLAYING);
        }
        PlayerData player = state.getPlayers().getFirst();
        player.getSpaceShip().getCabin(1).addCrewMember();
        Cabin c1 = new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        LifeSupportPurple lsp = new LifeSupportPurple(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        player.getSpaceShip().placeComponent(c1, 6,7);
        player.getSpaceShip().placeComponent(lsp, 6,8);
        player.getSpaceShip().getCabin(2).isValid();
        player.getSpaceShip().getCabin(2).addPurpleAlien();
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        for(Cabin c : player.getSpaceShip().getCabins().values()){
            cabinsID.add(new Pair<>(c.getID(), c.getCrewNumber()));
        }
        state.setCrewLoss(cabinsID);
        state.execute(player);
        assertThrows(IllegalStateException.class, () -> state.execute(player));
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
        AbandonedShip c2 = new AbandonedShip(2, 3, 3, 1, 4);
        AbandonedShipState emptyState = new AbandonedShipState(b, c2);
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
    void entry_doesNotThrowException() {
        assertDoesNotThrow(() -> state.entry());
    }

    @RepeatedTest(5)
    void execute_updatesStatusToPlayedIfPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        player.getSpaceShip().getCabin(1).addCrewMember();
        Cabin c1 = new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        LifeSupportPurple lsp = new LifeSupportPurple(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        player.getSpaceShip().placeComponent(c1, 6,7);
        player.getSpaceShip().placeComponent(lsp, 6,8);
        player.getSpaceShip().getCabin(2).isValid();
        player.getSpaceShip().getCabin(2).addPurpleAlien();
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        for(Cabin c : player.getSpaceShip().getCabins().values()){
            cabinsID.add(new Pair<>(c.getID(), c.getCrewNumber()));
        }
        state.setCrewLoss(cabinsID);
        state.execute(player);
        assertEquals(PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_updatesStatusToSkippedIfNotPlaying() {
        PlayerData player = state.getPlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        player.getSpaceShip().getCabin(1).addCrewMember();
        Cabin c1 = new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        LifeSupportPurple lsp = new LifeSupportPurple(3, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE});
        player.getSpaceShip().placeComponent(c1, 6,7);
        player.getSpaceShip().placeComponent(lsp, 6,8);
        player.getSpaceShip().getCabin(2).isValid();
        player.getSpaceShip().getCabin(2).addPurpleAlien();
        ArrayList<Pair<Integer, Integer>> cabinsID = new ArrayList<>();
        for(Cabin c : player.getSpaceShip().getCabins().values()){
            cabinsID.add(new Pair<>(c.getID(), c.getCrewNumber()));
        }
        state.setCrewLoss(cabinsID);
        state.execute(player);
        assertEquals(PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @RepeatedTest(5)
    void execute_withNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.execute(null));
    }

    @RepeatedTest(5)
    void exit_whenAllPlayersHavePlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @RepeatedTest(5)
    void exit_whenSomePlayersHaveNotPlayed() {
        state.setStatusPlayers(PlayerStatus.PLAYED);
        state.playersStatus.put(state.getPlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_whenPlayersSkippedAndPlayed() {
        state.setStatusPlayers(PlayerStatus.SKIPPED);
        state.playersStatus.put(state.getPlayers().getFirst().getColor(), PlayerStatus.PLAYED);
        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @Test
    void testConstructorWhenBoardIsNull() {
        assertThrows(NullPointerException.class, () -> {new State(null) {};});
    }
}