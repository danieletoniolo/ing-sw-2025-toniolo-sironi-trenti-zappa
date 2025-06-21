package it.polimi.ingsw.model.state;

import it.polimi.ingsw.controller.EventCallback;
import it.polimi.ingsw.controller.StateTransitionHandler;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.Planets;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.ConnectorType;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import it.polimi.ingsw.model.spaceship.Storage;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlanetsStateTest {
    Field planetsField = PlanetsState.class.getDeclaredField("planetSelected");
    Field cardField = PlanetsState.class.getDeclaredField("card");
    PlanetsState state;
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

    PlanetsStateTest() throws NoSuchFieldException {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        planetsField.setAccessible(true);
        cardField.setAccessible(true);

        SpaceShip ship0 = new SpaceShip(Level.SECOND, PlayerColor.BLUE);
        SpaceShip ship1 = new SpaceShip(Level.SECOND, PlayerColor.RED);
        SpaceShip ship2 = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        SpaceShip ship3 = new SpaceShip(Level.SECOND, PlayerColor.YELLOW);
        PlayerData p0 = new PlayerData("p0", "123e4567-e89b-12d3-a456-426614174001", PlayerColor.BLUE, ship0);
        PlayerData p1 = new PlayerData("p1", "123e4567-e89b-12d3-a456-426614174002", PlayerColor.RED, ship1);
        PlayerData p2 = new PlayerData("p2", "123e4567-e89b-12d3-a456-426614174003", PlayerColor.GREEN, ship2);
        PlayerData p3 = new PlayerData("p3", "123e4567-e89b-12d3-a456-426614174004", PlayerColor.YELLOW, ship3);

        Board board = new Board(Level.SECOND);
        board.clearInGamePlayers();
        board.setPlayer(p0, 0);
        board.setPlayer(p1, 1);
        board.setPlayer(p2, 2);
        board.setPlayer(p3, 3);

        List<List<Good>> planets = List.of((List.of(new Good(GoodType.YELLOW))), (List.of(new Good(GoodType.GREEN))));
        Planets c1 = new Planets(2, 1, planets, 3);

        state = new PlanetsState(board, ecb, c1, th);
        assertNotNull(state);
    }

    @Test
    void selectPlanet_withUnselectedPlanet() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        assertDoesNotThrow(() -> state.selectPlanet(player, 0));
        assertEquals(player, ((PlayerData[]) planetsField.get(state))[0]);
    }

    @Test
    void selectPlanet_withAlreadySelectedPlanet() {
        PlayerData player1 = state.board.getInGamePlayers().get(0);
        PlayerData player2 = state.board.getInGamePlayers().get(1);
        state.selectPlanet(player1, 0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> state.selectPlanet(player2, 0));
        assertTrue(exception.getMessage().contains(player1.getUsername()));
    }

    @Test
    void selectPlanet_withInvalidPlanetIndex() {
        PlayerData player = state.board.getInGamePlayers().getFirst();

        assertThrows(IllegalArgumentException.class, () -> state.selectPlanet(player, -1));
        assertThrows(IllegalArgumentException.class, () -> state.selectPlanet(player, ((Planets) cardField.get(state)).getPlanetNumbers()));
    }

    @Test
    void setGoodsToExchange_withValidExchangeData() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        ArrayList<Triplet<List<Good>, List<Good>, Integer>> exchangeData = new ArrayList<>();
        exchangeData.add(new Triplet<>(new ArrayList<>(), new ArrayList<>(), 1));
        PlayerData[] arr = (PlayerData[]) planetsField.get(state);
        arr[0] = player;
        planetsField.set(state, arr);

        assertDoesNotThrow(() -> state.setGoodsToExchange(player, exchangeData));
    }

    @Test
    void swapGoods() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        player.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        player.getSpaceShip().placeComponent(new Storage(2, null, true, 3), 6, 8);
        Good g1 = new Good(GoodType.YELLOW);
        Good g2 = new Good(GoodType.BLUE);
        player.getSpaceShip().exchangeGood(List.of(g1), null, 1);
        player.getSpaceShip().exchangeGood(List.of(g2), null, 2);
        PlayerData[] arr = (PlayerData[]) planetsField.get(state);
        arr[0] = player;
        planetsField.set(state, arr);

        assertDoesNotThrow(() -> state.swapGoods(player, 1, 2, List.of(g1), List.of(g2)));
        assertTrue(player.getSpaceShip().getStorage(1).getGoods().contains(g2));
        assertTrue(player.getSpaceShip().getStorage(2).getGoods().contains(g1));
    }

    @Test
    void entry_doesNotThrowException() {
        assertDoesNotThrow(() -> state.entry());
    }

    @Test
    void execute_withValidExchangeData() throws IllegalAccessException {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.replace(player.getColor(), State.PlayerStatus.PLAYING);
        PlayerData[] arr = (PlayerData[]) planetsField.get(state);
        arr[0] = player;
        planetsField.set(state, arr);
        ArrayList<Triplet<List<Good>, List<Good>, Integer>> exchangeData = new ArrayList<>();
        ConnectorType[] connector = new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.SINGLE, ConnectorType.SINGLE, ConnectorType.SINGLE};
        Storage s = new Storage(2, connector, true, 2);
        s.addGood(new Good(GoodType.YELLOW));
        s.addGood(new Good(GoodType.BLUE));
        player.getSpaceShip().placeComponent(s, 6,7);
        exchangeData.add(new Triplet<>(new ArrayList<>(), new ArrayList<>(), 2));
        state.setGoodsToExchange(player, exchangeData);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(State.PlayerStatus.PLAYED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void execute_withPlayerNotInPlayingState() {
        PlayerData player = state.board.getInGamePlayers().getFirst();
        state.playersStatus.replace(player.getColor(), State.PlayerStatus.WAITING);

        assertDoesNotThrow(() -> state.execute(player));
        assertEquals(State.PlayerStatus.SKIPPED, state.playersStatus.get(player.getColor()));
    }

    @Test
    void exit_withAllPlayersPlayed() throws IllegalAccessException {
        state.board.getInGamePlayers().forEach(player -> state.playersStatus.replace(player.getColor(), State.PlayerStatus.PLAYED));
        int initialSteps = state.board.getInGamePlayers().getFirst().getStep();
        int flightDays = ((Planets) cardField.get(state)).getFlightDays();

        assertDoesNotThrow(() -> state.exit());
        assertTrue(initialSteps - flightDays >= state.board.getInGamePlayers().getFirst().getStep());
    }

    @Test
    void exit_withPlayerInWaitingState_or_withPlayerInPlayingState() {
        state.playersStatus.replace(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.WAITING);
        assertThrows(IllegalStateException.class, () -> state.exit());

        state.playersStatus.replace(state.board.getInGamePlayers().getFirst().getColor(), State.PlayerStatus.PLAYING);
        assertThrows(IllegalStateException.class, () -> state.exit());
    }
}