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

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipStateTest {
    AbandonedShipState state;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        AbandonedShip c1 = new AbandonedShip(2, 3, 3, 1, 4);

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
    void setCrewLoss_withEmptyCabinID() {
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
        for(PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        }
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().getCabin(152).addCrewMember();
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

        PlayerData p = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(p));

        PlayerData p1 = new PlayerData("123e4567-e89b-12d3-a456-426614174005", PlayerColor.YELLOW, new SpaceShip(Level.SECOND, PlayerColor.YELLOW));
        assertThrows(IllegalStateException.class, () -> state.execute(p1));
    }

    @Test
    void execute_withPlayerAlreadyPlayed() {
        PlayerData p = state.board.getInGamePlayers().getFirst();
        state.setStatusPlayers(PlayerStatus.PLAYED);
        assertThrows(IllegalStateException.class, () -> state.execute(p));
    }

    @Test
    void execute_whenAlreadyPlayed() {
        for (PlayerData p : state.board.getInGamePlayers()) {
            state.playersStatus.put(p.getColor(), PlayerStatus.PLAYING);
        }
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().getCabin(152).addCrewMember();
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
    void entry_doesNotThrowException() {
        assertDoesNotThrow(() -> state.entry());
    }

    @RepeatedTest(5)
    void execute_updatesStatusToPlayedIfPlaying() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.PLAYING);
        player.getSpaceShip().getCabin(152).addCrewMember();
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
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.put(player.getColor(), PlayerStatus.WAITING);
        player.getSpaceShip().getCabin(152).addCrewMember();
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
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_whenPlayersSkippedAndPlayed() {
        state.setStatusPlayers(PlayerStatus.SKIPPED);
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), PlayerStatus.PLAYED);
        assertDoesNotThrow(() -> state.exit());
        assertTrue(state.played);
    }

    @Test
    void testConstructorWhenBoardIsNull() {
        assertThrows(NullPointerException.class, () -> new State(null) {});
    }
}