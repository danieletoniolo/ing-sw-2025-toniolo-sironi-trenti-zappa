package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.AbandonedShip;
import it.polimi.ingsw.model.game.board.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
    };
    StateTransitionHandler th = _ -> {
    };

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

        AbandonedShip c1 = new AbandonedShip(2, 3, 2, 1, 4);

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

    @Test
    void play_forValidPlayer() {
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));
        p0.getSpaceShip().addCrewMember(152, false, false);
        assertDoesNotThrow(() -> state.play(p0));
    }

    @Test
    void play_forNullPlayer() {
        assertThrows(NullPointerException.class, () -> state.play(null));
    }

    @Test
    void setPenaltyLoss_whenTypeIsInvalid() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(player, 99, cabinsID));
    }

    @Test
    void setPenaltyLoss_whenTypeIsZero() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 0, cabinsID));
    }

    @Test
    void setPenaltyLoss_whenTypeIsOne() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(player, 1, cabinsID));
    }

    @Test
    void setPenaltyLoss_whenTypeIs3() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        List<Integer> cabinsID = List.of(1, 2);

        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(player, 3, cabinsID));
    }

    @Test
    void execute_withNullPlayer_or_statePlayed() throws IllegalAccessException {
        assertThrows(NullPointerException.class, () -> state.execute(null));

        played.set(state, true);
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.execute(player));
    }

    @Test
    void execute_addsCoinsAndTriggersEventWhenPlayerIsPlaying() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(player.getColor(), State.PlayerStatus.PLAYING);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(((AbandonedShip) card.get(state)).getCredit(), player.getCoins());
    }

    @Test
    void exit_whenNotAllPlayersHavePlayed() throws IllegalAccessException {
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(((Board) board.get(state)).getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void exit_updatesPlayerPositionWhenAllPlayersHavePlayed() throws IllegalAccessException {
        PlayerData player = ((Board) board.get(state)).getInGamePlayers().getFirst();
        int initialStep = player.getStep();

        for(PlayerData p : ((Board) board.get(state)).getInGamePlayers()) {
            ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(p.getColor(), State.PlayerStatus.PLAYED);
        }
        assertDoesNotThrow(() -> state.exit());
        assertEquals(initialStep - ((AbandonedShip) card.get(state)).getFlightDays(), player.getStep());
    }

    @Test
    void exit_whenPlayersSkippedAndPlayed() throws IllegalAccessException {
        for(PlayerData p : ((Board) board.get(state)).getInGamePlayers()) {
            ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(p.getColor(), State.PlayerStatus.PLAYED);
        }
        ((Map<PlayerColor, State.PlayerStatus>) playersStatus.get(state)).put(((Board) board.get(state)).getInGamePlayers().getFirst().getColor(), State.PlayerStatus.SKIPPED);

        assertDoesNotThrow(() -> state.exit());
        assertTrue((Boolean) played.get(state));
    }


    //Test for state
    @Test
    void methodsState() throws IllegalAccessException {
        PlayerData p = ((Board) board.get(state)).getInGamePlayers().getFirst();
        LocalTime time = LocalTime.now();
        assertThrows(IllegalStateException.class, () -> {state.startGame(time, 0);});
        assertThrows(IllegalStateException.class, () -> {state.pickTile(p, 0, 0);});
        assertThrows(IllegalStateException.class, () -> {state.placeTile(p, 0, 6, 7);});
        assertThrows(IllegalStateException.class, () -> {state.useDeck(p, 0, 0);});
        assertThrows(IllegalStateException.class, () -> {state.rotateTile(p);});
        assertThrows(IllegalStateException.class, () -> {state.flipTimer(p);});
        assertThrows(IllegalStateException.class, () -> {state.placeMarker(p, 0);});
        assertThrows(IllegalStateException.class, () -> {state.manageCrewMember(p, 0, 0, 0);});
        assertThrows(IllegalStateException.class, () -> {state.useExtraStrength(p, 0, null, null);});
        assertThrows(IllegalStateException.class, () -> {state.setPenaltyLoss(p, 0, null);});
        assertThrows(IllegalStateException.class, () -> {state.selectPlanet(p, 0);});
        assertThrows(IllegalStateException.class, () -> {state.setFragmentChoice(p, 0);});
        assertThrows(IllegalStateException.class, () -> {state.setComponentToDestroy(p, null);});
        assertThrows(IllegalStateException.class, () -> {state.rollDice(p);});
        assertThrows(IllegalStateException.class, () -> {state.setProtect(p, 0);});
        assertThrows(IllegalStateException.class, () -> {state.setGoodsToExchange(p, null);});
        assertThrows(IllegalStateException.class, () -> {state.swapGoods(p, 1, 2, null, null);});
        assertThrows(IllegalStateException.class, () -> {state.cheatCode(p, 0);});
    }

    @Test
    void giveUpWhenPlayerIsNull() throws JsonProcessingException {
        State state = new AbandonedShipState(new Board(Level.SECOND), ecb, new AbandonedShip(2, 3, 3, 1, 4), th);
        assertThrows(NullPointerException.class, () -> state.giveUp(null));
    }

    @Test
    void giveUpValidPlayer() throws JsonProcessingException {
        State state = new AbandonedShipState(new Board(Level.SECOND), ecb, new AbandonedShip(2, 3, 3, 1, 4), th);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.BLUE, new SpaceShip(Level.SECOND, PlayerColor.BLUE));
        assertDoesNotThrow(() -> state.giveUp(player));
    }

    @Test
    void gameState_fromInt(){
        assertEquals(GameState.CARDS, GameState.fromInt(4));
        assertThrows(IllegalArgumentException.class, () -> GameState.fromInt(99));
    }
}