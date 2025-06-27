package it.polimi.ingsw.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.hits.Direction;
import it.polimi.ingsw.model.cards.hits.Hit;
import it.polimi.ingsw.model.cards.hits.HitType;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.*;
import it.polimi.ingsw.model.state.*;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    Field instanceField = MatchController.class.getDeclaredField("instance");

    GameControllerTest() throws JsonProcessingException, NoSuchFieldException {
    }

    @Test
    void UUID_methods() throws IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        assertNotNull(gameController.getUUID());
        assertNotNull(gameController.toString());
        assertDoesNotThrow(() -> gameController.changeState(new CrewState(b, new ServerEventManager(li), null)));
    }

    @ParameterizedTest
    @CsvSource({
            "10:00, 30",
            "23:59, 1"
    })
    void startGame_initializesGameWithValidStartTimeAndTimerDuration(String startTime, int timerDuration) throws JsonProcessingException, NoSuchFieldException, IllegalAccessException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        LocalTime time = LocalTime.parse(startTime);
        gameController.startGame(time, timerDuration);
    }

    @ParameterizedTest
    @CsvSource({
            "10:00, -1"
    })
    void startGame_forInvalidTimerDuration(String startTime, int timerDuration) throws IllegalAccessException, NoSuchFieldException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        LocalTime time = LocalTime.parse(startTime);
        assertThrows(IllegalStateException.class, () -> gameController.startGame(time, timerDuration));
    }

    @Test
    void manageLobby_allowsValidPlayerAndType() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);

        gameController.manageLobby(player, 1);
    }

    @Test
    void manageLobby_forInvalidType() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);

        assertThrows(IllegalStateException.class, () -> gameController.manageLobby(player, -1));
    }

    @Test
    void play_allowsValidPlayerToPlay() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        assertDoesNotThrow(() -> gameController.play(player));
    }

    @Test
    void endTurn_allowsCurrentPlayerToEndTurn() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);

        gameController.endTurn(player);
    }

    @Test
    void endTurn_ifNotCurrentPlayer() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        assertThrows(IllegalStateException.class, () -> gameController.endTurn(player));
    }

    @Test
    void useDeck_allowsValidUsageAndDeckIndex() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.useDeck(player, 0, 0);
    }

    @Test
    void useDeck_forInvalidUsageOrDeckIndex() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.useDeck(player, -1, -1);
    }

    @Test
    void pickTile_allowsValidTileSelection() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.pickTile(player, 2, 2);
    }

    @Test
    void pickTile_forInvalidFromWhereOrTileID() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        assertThrows(IllegalStateException.class, () -> gameController.pickTile(player, 2, 1));
    }

    @Test
    void placeTile_allowsValidPlacement() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.pickTile(player, 2, 2);
        gameController.placeTile(player, 2, 6, 7);
    }

    @Test
    void placeTile_forInvalidPlacement() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, null), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        assertThrows(IllegalStateException.class, () -> gameController.placeTile(player, 2, 6, 7));
    }

    @Test
    void rotateTile_allowsValidPlayerToRotateTile() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.pickTile(player, 2, 2);
        gameController.rotateTile(player);
    }

    @Test
    void rotateTile_forNullOrInvalidPlayer() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.pickTile(player, 2, 2);
        assertThrows(IllegalStateException.class, () -> gameController.rotateTile(null));
    }

    @Test
    void placeMarker_() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        assertThrows(IllegalStateException.class, () -> gameController.placeMarker(player, 1));
    }

    @Test
    void placeMarker() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        assertThrows(IllegalStateException.class, () -> gameController.placeMarker(player, 5));
    }

    @Test
    void manageCrewMember_allowsValidCrewManagement() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new CrewState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.manageCrewMember(player, 0, 0, 153);
    }

    @Test
    void manageCrewMember_forInvalidParameters() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new CrewState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        assertThrows(IllegalStateException.class, () -> gameController.manageCrewMember(player, 0, -1, -1));
    }

    @Test
    void flipTimer_allowsValidPlayerToFlipTimer() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        EventCallback ecb = new ServerEventManager(li);
        StateTransitionHandler th = null;
        stateField.set(gameController, new BuildingState(new Board(Level.SECOND), ecb, th));
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        s.placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE, ConnectorType.TRIPLE}), 6,7);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        gameController.flipTimer(player);
    }

    @Test
    void giveUp_allowsCurrentPlayerToGiveUp() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("p0", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();
        assertDoesNotThrow(() -> gameController.giveUp(p.getUUID()));
    }


    @Test
    void giveUp_forInvalidOrNullUUID() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        assertThrows(IllegalStateException.class, () -> gameController.giveUp(null));
    }

    @Test
    void selectPlanet_allowsValidPlayerToSelectPlanet() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("p0", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new PlanetsState(b, new ServerEventManager(li), new Planets(1, 2, List.of(List.of(new Good(GoodType.BLUE))), 3), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();

        assertDoesNotThrow(() -> gameController.selectPlanet(p.getUUID(), 0));
    }

    @Test
    void exchangeGoods_allowsValidExchange() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        Good g1 = new Good(GoodType.BLUE);
        stateField.set(gameController, new PlanetsState(b, new ServerEventManager(li), new Planets(1, 2, List.of(List.of(g1)), 3), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();
        p.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        Good g2 = new Good(GoodType.BLUE);
        p.getSpaceShip().exchangeGood(List.of(g2), null, 1);
        List<Triplet<List<Good>, List<Good>, Integer>> exchangeData = List.of(new Triplet<>(List.of(g1), List.of(g2), 1));
        gameController.selectPlanet(p.getUUID(), 0);
        assertDoesNotThrow(() -> gameController.exchangeGoods(p.getUUID(), exchangeData));
    }

    @Test
    void swapGoods_allowsValidSwap() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new PlanetsState(b, new ServerEventManager(li), new Planets(1, 2, List.of(List.of(new Good(GoodType.BLUE))), 3), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();
        p.getSpaceShip().placeComponent(new Storage(1, null, true, 3), 6, 7);
        Good g1 = new Good(GoodType.BLUE);
        p.getSpaceShip().exchangeGood(List.of(g1), null, 1);
        p.getSpaceShip().placeComponent(new Storage(2, null, true, 3), 6, 8);
        Good g2 = new Good(GoodType.YELLOW);
        p.getSpaceShip().exchangeGood(List.of(g2), null, 2);

        List<Good> goods1to2 = List.of(g1);
        List<Good> goods2to1 = List.of(g2);
        assertDoesNotThrow(() -> gameController.swapGoods(p.getUUID(), 1, 2, goods1to2, goods2to1));
    }

    @Test
    void useExtraStrength_allowsValidUsage() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new OpenSpaceState(b, new ServerEventManager(li), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);        PlayerData p = state.getCurrentPlayer();
        p.getSpaceShip().placeComponent(new Engine(2, null, 3), 6, 7);
        p.getSpaceShip().placeComponent(new Battery(3, null, 3), 6, 8);


        List<Integer> idsList = List.of(2);
        List<Integer> batteriesList = List.of(3);
        assertDoesNotThrow(() -> gameController.useExtraStrength(p.getUUID(), 0, idsList, batteriesList));
    }

    @Test
    void setPenaltyLoss() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new AbandonedShipState(b, new ServerEventManager(li), new AbandonedShip(1, 2, 3, 4, 5), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);        PlayerData p = state.getCurrentPlayer();
        p.getSpaceShip().placeComponent(new Cabin(2, null), 6, 7);

        List<Integer> penaltyLossList = List.of(2);
        assertThrows(IllegalStateException.class, () -> gameController.setPenaltyLoss(p.getUUID(), 0, penaltyLossList));
    }

    @Test
    void rollDice() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);

        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, null);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ArrayList<PlayerData> pl = new ArrayList<>();
        pl.add(player);
        playersField.set(stateField.get(gameController), pl);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();
        stateField.set(gameController, new PiratesState(b, new ServerEventManager(li), new Pirates(1, 2, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)), 1, 1, 1), null));

        assertThrows(IllegalStateException.class, () -> gameController.rollDice(p));
    }

    @Test
    void setFragmentChoice() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new ValidationState(b, new ServerEventManager(li), null));
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        player.getSpaceShip().placeComponent(new Cabin(2, null), 6, 7);
        player.getSpaceShip().placeComponent(new Cabin(3, null), 6, 8);
        player.getSpaceShip().destroyComponent(6, 7);

        assertThrows(IllegalStateException.class, () -> gameController.setFragmentChoice(player, 0));
    }

    @Test
    void setComponentToDestroy() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new ValidationState(b, new ServerEventManager(li), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        player.getSpaceShip().placeComponent(new Cabin(2, new ConnectorType[]{ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY}), 6, 7);
        player.getSpaceShip().getCabin(2).isValid();

        List<Pair<Integer, Integer>> componentsToDestroy = List.of(new Pair<>(6, 7));
        assertThrows(IllegalStateException.class, () -> gameController.setComponentToDestroy(player, componentsToDestroy));
    }

    @Test
    void setProtect() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new PiratesState(b, new ServerEventManager(li), new Pirates(1, 1, List.of(new Hit(HitType.SMALLMETEOR, Direction.NORTH)), 1, 1, 1), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        Field playersStatusField = State.class.getDeclaredField("playersStatus");
        playersStatusField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(stateField.get(gameController))).add(player);
        ((Map<PlayerColor, State.PlayerStatus>) playersStatusField.get(stateField.get(gameController))).put(player.getColor(), State.PlayerStatus.WAITING);
        PlayerData p = state.getCurrentPlayer();
        p.getSpaceShip().placeComponent(new Battery(1, null, 3), 6, 7);

        List<Integer> batteryIDList = List.of(1);
        assertThrows(IllegalStateException.class, () -> gameController.setProtect(p, batteryIDList));
    }

    @Test
    void cheatCode_allowsValidShipIndex() throws NoSuchFieldException, IllegalAccessException, JsonProcessingException {
        instanceField.setAccessible(true);
        instanceField.set(MatchController.getInstance(), null);
        NetworkTransceiver serverTransceiver = new NetworkTransceiver();
        MatchController.setUp(serverTransceiver);
        Board b = new Board(Level.SECOND);
        LobbyInfo li = new LobbyInfo("TestLobby", 4, Level.SECOND);
        NetworkTransceiver clientTransceiver = new NetworkTransceiver();
        GameController gameController = new GameController(b, li);

        Field networkTransceiversField = MatchController.class.getDeclaredField("networkTransceivers");
        networkTransceiversField.setAccessible(true);
        Map<LobbyInfo, NetworkTransceiver> a = new HashMap<>();
        a.put(li, clientTransceiver);
        networkTransceiversField.set(MatchController.getInstance(), a);
        SpaceShip s = new SpaceShip(Level.SECOND, PlayerColor.GREEN);
        PlayerData player = new PlayerData("Player1", UUID.randomUUID().toString(), PlayerColor.GREEN, s);
        Field stateField = GameController.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(gameController, new BuildingState(b, new ServerEventManager(li), null));
        State state = (State) stateField.get(gameController);
        Field playersField = State.class.getDeclaredField("players");
        playersField.setAccessible(true);
        ((ArrayList<PlayerData>) playersField.get(state)).add(player);

        assertDoesNotThrow(() -> gameController.cheatCode(player, 0));
    }




}