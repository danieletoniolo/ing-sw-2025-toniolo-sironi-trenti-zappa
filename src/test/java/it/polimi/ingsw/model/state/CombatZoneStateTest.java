package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.CombatZone;
import it.polimi.ingsw.model.cards.hits.Direction;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.cards.hits.HitType;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CombatZoneStateTest {
    Field internalStateField = CombatZoneState.class.getDeclaredField("internalState");
    Field cardField = CombatZoneState.class.getDeclaredField("card");
    Field fragmentsField = CombatZoneState.class.getDeclaredField("fragments");
    Field minPlayerCrewField = CombatZoneState.class.getDeclaredField("minPlayerCrew");
    Field minPlayerEnginesField = CombatZoneState.class.getDeclaredField("minPlayerEngines");
    Field minPlayerCannonsField = CombatZoneState.class.getDeclaredField("minPlayerCannons");
    Field playersStatusField = State.class.getDeclaredField("playersStatus");
    Field currentPenaltyLossField = CombatZoneState.class.getDeclaredField("currentPenaltyLoss");

    CombatZoneState state;
    CombatZoneState stateL;

    EventCallback ecb = new EventCallback() {;
        @Override
        public void trigger(Event event) {
        }

        @Override
        public void trigger(Event event, UUID targetUser) {

        }
        @Override
        public void triggerEndGame() {

        }
    };
    StateTransitionHandler th = _ -> {
    };

    CombatZoneStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        internalStateField.setAccessible(true);
        cardField.setAccessible(true);
        fragmentsField.setAccessible(true);
        minPlayerCrewField.setAccessible(true);
        minPlayerEnginesField.setAccessible(true);
        minPlayerCannonsField.setAccessible(true);
        playersStatusField.setAccessible(true);
        currentPenaltyLossField.setAccessible(true);

        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        PlayerData p00 = new PlayerData("p00", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p11 = new PlayerData("p01", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p22 = new PlayerData("p02", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p33 = new PlayerData("p03", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        Board board1 = new Board(Level.LEARNING);
        board1.clearInGamePlayers();
        board1.setPlayer(p00, 0);
        board1.setPlayer(p11, 1);
        board1.setPlayer(p22, 2);
        board1.setPlayer(p33, 3);

        ArrayList<Hit> fires = new ArrayList<>(List.of(new Hit(HitType.HEAVYFIRE, Direction.NORTH), new Hit(HitType.LIGHTFIRE, Direction.SOUTH), new Hit(HitType.SMALLMETEOR, Direction.EAST)));
        CombatZone c1 = new CombatZone(2, 1, fires, 2, 4);
        CombatZone c2 = new CombatZone(2, 1, fires, 1, 4);

        state = new CombatZoneState(board, ecb, c1, th);
        stateL = new CombatZoneState(board1, ecb, c2, th);
        assertNotNull(state);
        assertNotNull(stateL);
    }

    @Test
    void setFragmentChoice_removeFragments() throws Exception {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.HIT_PENALTY);

        List<List<Pair<Integer, Integer>>> fragments = new ArrayList<>();
        fragments.add(List.of(Pair.with(6, 7)));
        fragments.add(List.of(Pair.with(6, 8)));
        fragmentsField.set(state, fragments);

        PlayerData player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 8);
        state.setFragmentChoice(player, 0);

        assertTrue(((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).isEmpty());

        assertThrows(IllegalArgumentException.class, () -> state.setFragmentChoice(player, 0));
    }

    @Test
    void setFragmentChoice_withFragmentsEmpty() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CANNONS);
        PlayerData player = state.players.getFirst();
        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(player, 0));
    }

    @Test
    void setFragmentChoice_internalStateInvalid() throws Exception {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        assertThrows(IllegalStateException.class, () -> state.setFragmentChoice(player, 0));
    }

    @Test
    void setProtect_whenInternalStateIsInvalid() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();

        assertThrows(IllegalStateException.class, () -> state.setProtect(player, List.of(1)));
    }

    @Test
    void setProtect_triggersEventWhenValidBatteryID() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CAN_PROTECT);
        PlayerData player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(3, null, 3), 6, 8);
        player.getSpaceShip().placeComponent(new Battery(2, null, 3), 6, 9);
        player.getSpaceShip().destroyComponent(6, 8);
        ((List<List<Pair<Integer, Integer>>>) fragmentsField.get(state)).add(List.of(Pair.with(6, 7)));

        state.rollDice(player);
        state.execute(player);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.HIT_PENALTY);
        assertDoesNotThrow(() -> state.setProtect(player, List.of(-1)));
        assertDoesNotThrow(() -> state.setFragmentChoice(player, 0));
        assertTrue(((List<?>) fragmentsField.get(state)).isEmpty());
    }

    @Test
    void rollDice_triggersEventsForValidPlayer() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CAN_PROTECT);
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 7);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 8);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 5);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 4);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 9);
        player.getSpaceShip().placeComponent(new Connectors(1, null), 6, 3);

        assertDoesNotThrow(() -> state.rollDice(player));
    }

    @Test
    void rollDice_internalStateInvalid() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertThrows(IllegalStateException.class, () -> state.rollDice(player));
    }

    @Test
    void setPenaltyLoss_type0() throws Exception {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.BATTERIES_PENALTY);
        PlayerData player = state.players.getFirst();
        List<Integer> penaltyLoss = List.of(1, 2, 3);
        PlayerData finalPlayer = player;
        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(finalPlayer, 0, penaltyLoss));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.GOODS_PENALTY);
        player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        player.getSpaceShip().exchangeGood(List.of(new Good(GoodType.YELLOW), new Good(GoodType.GREEN)), null, 1);
        PlayerData finalPlayer1 = player;
        assertDoesNotThrow(() -> state.setPenaltyLoss(finalPlayer1, 0, List.of(1)));
    }

    @Test
    void setPenaltyLoss_type1() throws Exception {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        List<Integer> penaltyLoss = List.of(1, 2, 3);
        PlayerData finalPlayer = player;
        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(finalPlayer, 1, penaltyLoss));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.BATTERIES_PENALTY);
        player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Cannon(1, null, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 8);
        PlayerData finalPlayer1 = player;
        minPlayerCrewField.set(state, player);
        assertDoesNotThrow(() -> state.setPenaltyLoss(finalPlayer1, 1, List.of(1)));
    }

    @Test
    void setPenaltyLoss_type2() throws Exception {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        List<Integer> penaltyLoss = List.of(1, 2, 3);
        PlayerData finalPlayer = player;
        assertThrows(IllegalStateException.class, () -> state.setPenaltyLoss(finalPlayer, 2, penaltyLoss));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CREW_PENALTY);
        player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().addCrewMember(1, false, false);
        player.getSpaceShip().addCrewMember(152, false, false);
        PlayerData finalPlayer1 = player;
        assertDoesNotThrow(() -> state.setPenaltyLoss(finalPlayer1, 2, List.of(1)));
    }

    @Test
    void setPenaltyLoss_type3() {
        assertThrows(IllegalArgumentException.class, () -> state.setPenaltyLoss(state.players.getFirst(), 3, List.of()));
    }

    @Test
    void useExtraStrength_type0() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CANNONS);
        PlayerData player = state.players.getFirst();
        PlayerData finalPlayer = player;
        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(finalPlayer, 0, List.of(), List.of()));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(2, null, 3), 6, 8);
        PlayerData finalPlayer1 = player;
        minPlayerEnginesField.set(state, player);
        assertDoesNotThrow(() -> state.useExtraStrength(finalPlayer1, 0, List.of(1), List.of(2)));
    }

    @Test
    void useExtraStrength_type1() throws IllegalAccessException {
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        PlayerData player = state.players.getFirst();
        PlayerData finalPlayer = player;
        assertThrows(IllegalStateException.class, () -> state.useExtraStrength(finalPlayer, 1, List.of(), List.of()));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CANNONS);
        player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Cannon(1, null, 1), 6, 7);
        player.getSpaceShip().placeComponent(new Battery(2, null, 3), 6, 8);
        PlayerData finalPlayer1 = player;
        minPlayerCannonsField.set(state, player);
        assertDoesNotThrow(() -> state.useExtraStrength(finalPlayer1, 1, List.of(1), List.of(2)));
    }

    @Test
    void useExtraStrength_type2() {
        assertThrows(IllegalArgumentException.class, () -> state.useExtraStrength(state.players.getFirst(), 2, List.of(), List.of()));
    }

    @Test
    void entry_checkMinPlayerCrew() throws IllegalAccessException {
        PlayerData p0 = state.players.getFirst();
        p0.getSpaceShip().addCrewMember(152, false, false);
        PlayerData p1 = state.players.get(1);
        p1.getSpaceShip().addCrewMember(154, false, false);
        p1.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        p1.getSpaceShip().addCrewMember(1, false, false);
        PlayerData p2 = state.players.get(2);
        PlayerData p3 = state.players.getLast();
        p3.getSpaceShip().addCrewMember(155, false, false);
        p3.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        p3.getSpaceShip().addCrewMember(1, false, false);
        p3.getSpaceShip().placeComponent(new Cabin(2, null), 6, 8);
        p3.getSpaceShip().addCrewMember(2, false, false);

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CREW);
        assertDoesNotThrow(() -> state.entry());
        PlayerData expectedMinPlayer = state.players.stream()
                .min(Comparator.comparingInt(p -> p.getSpaceShip().getCrewNumber()))
                .orElse(null);
        assertEquals(expectedMinPlayer, minPlayerCrewField.get(state));
        assertEquals(p2, minPlayerCrewField.get(state));
    }

    @Test
    void entry_checkMinPlayeEngine() throws IllegalAccessException {
        PlayerData p0 = state.players.getFirst();
        p0.getSpaceShip().addCrewMember(152, false, false);
        PlayerData p1 = state.players.get(1);
        p1.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        PlayerData p2 = state.players.get(2);
        p2.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        PlayerData p3 = state.players.getLast();
        p3.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        p3.getSpaceShip().placeComponent(new Engine(2, null, 1), 6, 8);

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        assertDoesNotThrow(() -> state.entry());
        PlayerData expectedMinPlayer = state.players.stream()
                .min(Comparator.comparingDouble(p -> p.getSpaceShip().getSingleEnginesStrength()))
                .orElse(null);
        assertEquals(expectedMinPlayer, minPlayerEnginesField.get(state));
        assertEquals(p0, minPlayerEnginesField.get(state));
    }

    @Test
    void entry_checkMinPlayeCannons() throws IllegalAccessException {
        PlayerData p0 = state.players.getFirst();
        p0.getSpaceShip().addCrewMember(152, false, false);
        PlayerData p1 = state.players.get(1);
        p1.getSpaceShip().placeComponent(new Cannon(1, null, 1), 6, 7);
        PlayerData p2 = state.players.get(2);
        p2.getSpaceShip().placeComponent(new Cannon(1, null, 1), 6, 7);
        PlayerData p3 = state.players.getLast();
        p3.getSpaceShip().placeComponent(new Cannon(1, null, 1), 6, 7);
        p3.getSpaceShip().placeComponent(new Cannon(2, null, 1), 6, 8);

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CANNONS);
        assertDoesNotThrow(() -> state.entry());
        PlayerData expectedMinPlayer = state.players.stream()
                .min(Comparator.comparingDouble(p -> p.getSpaceShip().getSingleCannonsStrength()))
                .orElse(null);
        assertEquals(expectedMinPlayer, minPlayerCannonsField.get(state));
        assertEquals(p0, minPlayerCannonsField.get(state));
    }

    //TODO: Testare dopo che hanno finito entry
    @Test
    void entry_cardLevel1() throws IllegalAccessException {
        minPlayerCannonsField.set(stateL, stateL.players.getFirst());
        minPlayerEnginesField.set(stateL, stateL.players.get(1));
        minPlayerCrewField.set(stateL, stateL.players.get(2));
        stateL.entry();

        assertNull(((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateL)).get((PlayerData) minPlayerCrewField.get(stateL)));
    }

    @Test
    void execute_Crew() throws IllegalAccessException {
        PlayerData player = state.players.getFirst();
        player.getSpaceShip().addCrewMember(152, false, false);
        player.getSpaceShip().placeComponent(new Cabin(1, null), 6, 7);
        player.getSpaceShip().addCrewMember(1, false, false);
        minPlayerCrewField.set(state, state.players.getFirst());
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CREW);
        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(CombatZoneState.CombatZoneInternalState.CAN_PROTECT, internalStateField.get(state));

        internalStateField.set(stateL, CombatZoneState.CombatZoneInternalState.CREW);
        PlayerData player1 = stateL.players.getFirst();
        minPlayerCrewField.set(stateL, player1);
        assertDoesNotThrow(() -> stateL.execute(player1));
        assertEquals(State.PlayerStatus.PLAYING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateL)).get(player1.getColor()));
        assertEquals(CombatZoneState.CombatZoneInternalState.ENGINES, internalStateField.get(stateL));
    }

    @Test
    void execute_Engines() throws IllegalAccessException {
        PlayerData player = state.players.getLast();
        player.getSpaceShip().placeComponent(new Engine(1, null, 1), 6, 7);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.ENGINES);
        minPlayerEnginesField.set(state, state.players.getFirst());
        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(CombatZoneState.CombatZoneInternalState.GOODS_PENALTY, internalStateField.get(state));

        internalStateField.set(stateL, CombatZoneState.CombatZoneInternalState.ENGINES);
        minPlayerEnginesField.set(stateL, stateL.players.getFirst());
        assertDoesNotThrow(() -> stateL.execute(player));
        assertEquals(CombatZoneState.CombatZoneInternalState.CREW_PENALTY, internalStateField.get(stateL));
    }

    @Test
    void execute_GoodsPenalty() throws IllegalAccessException {
        currentPenaltyLossField.set(state, 1);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.GOODS_PENALTY);
        PlayerData player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        player.getSpaceShip().exchangeGood(List.of(new Good(GoodType.YELLOW), new Good(GoodType.GREEN)), null, 1);
        assertThrows(NullPointerException.class, () -> state.execute(player));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.GOODS_PENALTY);
        currentPenaltyLossField.set(state, 1);
        PlayerData player2 = state.players.get(1);
        minPlayerEnginesField.set(state, state.players.get(1));
        assertDoesNotThrow(() -> state.execute(player2));
        assertEquals(CombatZoneState.CombatZoneInternalState.BATTERIES_PENALTY, internalStateField.get(state));

        currentPenaltyLossField.set(state, 0);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.GOODS_PENALTY);
        PlayerData player1 = state.players.getFirst();
        minPlayerCrewField.set(state, player1);
        assertDoesNotThrow(() -> state.execute(player1));
        assertEquals(State.PlayerStatus.PLAYING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(state)).get(player1.getColor()));
        assertEquals(CombatZoneState.CombatZoneInternalState.CREW, internalStateField.get(state));
    }

    @Test
    void execute_BatteriesPenalty() throws IllegalAccessException {
        currentPenaltyLossField.set(state, 1);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.BATTERIES_PENALTY);
        PlayerData player = state.players.getFirst();
        player.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 7);
        assertThrows(NullPointerException.class, () -> state.execute(player));

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.BATTERIES_PENALTY);
        PlayerData player2 = state.players.get(1);
        currentPenaltyLossField.set(state, 0);
        minPlayerCrewField.set(state, player2);
        assertDoesNotThrow(() -> state.execute(player2));
        assertEquals(CombatZoneState.CombatZoneInternalState.CREW, internalStateField.get(state));
        assertEquals(State.PlayerStatus.PLAYING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(state)).get(player2.getColor()));
    }

    @Test
    void execute_CrewPenalty() throws IllegalAccessException {
        currentPenaltyLossField.set(state, 1);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CREW_PENALTY);
        PlayerData player = state.players.getFirst();
        assertDoesNotThrow(() -> state.execute(player));

        currentPenaltyLossField.set(state, 0);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CREW_PENALTY);
        PlayerData player2 = state.players.get(1);
        assertDoesNotThrow(() -> state.execute(player2));
        assertEquals(CombatZoneState.CombatZoneInternalState.GIVE_UP, internalStateField.get(state));
        assertEquals(State.PlayerStatus.WAITING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(state)).get(player2.getColor()));
    }

    @Test
    void execute_HitPenalty() throws NoSuchFieldException, IllegalAccessException {
        Field hitIndexField = CombatZoneState.class.getDeclaredField("hitIndex");
        hitIndexField.setAccessible(true);
        hitIndexField.set(state, 0);

        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.HIT_PENALTY);
        PlayerData player = state.players.getFirst();
        assertDoesNotThrow(() -> state.execute(player));

        hitIndexField.set(state, ((CombatZone) cardField.get(state)).getFires().size());
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.HIT_PENALTY);
        PlayerData player2 = state.players.get(1);
        assertDoesNotThrow(() -> state.execute(player2));
    }

    @Test
    void execute_Cannon() throws IllegalAccessException {
        PlayerData p = state.players.getLast();
        minPlayerCannonsField.set(state, p);
        internalStateField.set(state, CombatZoneState.CombatZoneInternalState.CANNONS);
        assertDoesNotThrow(() -> state.execute(p));
        assertEquals(CombatZoneState.CombatZoneInternalState.ENGINES, internalStateField.get(state));
        assertEquals(State.PlayerStatus.PLAYING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(state)).get(p.getColor()));

        PlayerData p1 = stateL.players.getLast();
        minPlayerCannonsField.set(stateL, p1);
        internalStateField.set(stateL, CombatZoneState.CombatZoneInternalState.CANNONS);
        assertDoesNotThrow(() -> stateL.execute(p1));
        assertEquals(CombatZoneState.CombatZoneInternalState.CAN_PROTECT, internalStateField.get(stateL));
        assertEquals(State.PlayerStatus.PLAYING, ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateL)).get(p1.getColor()));
    }

    @Test
    void exit_withAllPlayersPlayed() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }

        assertDoesNotThrow(() -> state.exit());
    }

    @Test
    void exit_withWaitingPlayer() {
        for (PlayerData player : state.board.getInGamePlayers()) {
            state.playersStatus.put(player.getColor(), State.PlayerStatus.PLAYED);
        }
        state.playersStatus.put(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);

        assertThrows(IllegalStateException.class, () -> state.exit());
    }

    @Test
    void exit_withNoPlayers() {
        state.players.clear();
        state.playersStatus.clear();

        assertDoesNotThrow(() -> state.exit());
        assertFalse(state.played);
    }
}