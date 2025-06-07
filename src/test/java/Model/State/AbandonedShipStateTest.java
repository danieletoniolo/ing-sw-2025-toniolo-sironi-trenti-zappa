package Model.State;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.model.cards.AbandonedShip;
import it.polimi.ingsw.model.game.board.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.model.state.AbandonedShipState;
import it.polimi.ingsw.model.state.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipStateTest {
    AbandonedShipState state;
    Field card = AbandonedShipState.class.getDeclaredField("card");
    Field played = State.class.getDeclaredField("played");
    Field playersStatus = State.class.getDeclaredField("playersStatus");
    Field board = State.class.getDeclaredField("board");
    Field transitionHandler = State.class.getDeclaredField("transitionHandler");
    Field eventCallback = State.class.getDeclaredField("eventCallback");
    Field players = State.class.getDeclaredField("players");

    EventCallback ecb;
    StateTransitionHandler th;

    AbandonedShipStateTest() throws NoSuchFieldException {
    }


    @BeforeEach
    void setUp() throws JsonProcessingException {
        card.setAccessible(true);
        played.setAccessible(true);
        playersStatus.setAccessible(true);
        board.setAccessible(true);
        transitionHandler.setAccessible(true);
        eventCallback.setAccessible(true);
        players.setAccessible(true);

        AbandonedShip c1 = new AbandonedShip(2, 3, 3, 1, 4);

        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board1 = new Board(Level.SECOND);
        board1.clearInGamePlayers();
        board1.setPlayer(p0, 0);
        board1.setPlayer(p1, 1);
        board1.setPlayer(p2, 2);
        board1.setPlayer(p3, 3);

        state = new AbandonedShipState(board1, ecb, c1, th);
        assertNotNull(state);
    }

    @RepeatedTest(5)
    void setPenaltyLoss_whenTypeIsInvalid() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(player, 99, cabinsID));
    }

    @RepeatedTest(5)
    void setPenaltyLoss_whenTypeIsZero() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 0, cabinsID));
    }

    @RepeatedTest(5)
    void setPenaltyLoss_whenTypeIsOne() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 1, cabinsID));
    }

    @RepeatedTest(5)
    void setPenaltyLoss_triggersEventWhenTypeIsTwo() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertDoesNotThrow(() -> state.setPenaltyLoss(player, 2, cabinsID));
    }

    @Test
    void execute_withNullPlayer_or_statePlayed() throws IllegalAccessException {
        assertThrows(NullPointerException.class, () -> state.execute(null));

        played.set(state, true);
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @RepeatedTest(5)
    void execute_addsCoinsAndTriggersEventWhenPlayerIsPlaying() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(player.getColor(), State.PlayerStatus.PLAYING);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(((AbandonedShip) card.get(state)).getCredit(), player.getCoins());
    }

    @RepeatedTest(5)
    void execute_callsSuperExecuteAndTransitionsToNextState() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(player.getColor(), State.PlayerStatus.PLAYING);

        assertDoesNotThrow(() -> state.execute(player));
        assertTrue(played.getBoolean(state));
    }

    @RepeatedTest(5)
    void exit_whenNotAllPlayersHavePlayed() throws IllegalAccessException {
        playersStatus.set(state, State.PlayerStatus.PLAYED);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(((Board) board.get(state)).getInGamePlayers().getFirst().getColor(), PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @RepeatedTest(5)
    void exit_updatesPlayerPositionAndTriggersEventWhenAllPlayersHavePlayed() throws IllegalAccessException {
        playersStatus.set(state, PlayerStatus.PLAYED);
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        int initialStep = player.getStep();

        assertDoesNotThrow(() -> state.exit());
        assertEquals(initialStep - ((AbandonedShip) card.get(state)).getFlightDays(), player.getStep());
    }

    @RepeatedTest(5)
    void exit_doesNotThrowExceptionWhenPlayersSkippedAndPlayed() throws IllegalAccessException {
        playersStatus.set(state, PlayerStatus.SKIPPED);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(((Board) board.get(state)).getInGamePlayers().getFirst().getColor(), PlayerStatus.PLAYED);

        assertDoesNotThrow(() -> state.exit());
        assertTrue((Boolean) played.get(state));
    }
    
    //TODO: Fare test per state e rivedere test sotto

    /*

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

     */
}