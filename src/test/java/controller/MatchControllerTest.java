package controller;

import Model.Game.Lobby.LobbyInfo;
import network.Connection;
import network.User;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.ZeroArgMessage;
import network.rmi.RMIConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchControllerTest {
    MatchController mc;

    @BeforeEach
    void setUp() {
        mc = new MatchController();
    }

    //Because static
    @Test
    void getInstance_returnsSameInstanceOnMultipleCalls() {
        MatchController firstInstance = MatchController.getInstance();
        MatchController secondInstance = MatchController.getInstance();

        assertEquals(firstInstance, secondInstance);
    }

    @Test
    void getInstance_createsNonNullInstance() {
        MatchController instance = MatchController.getInstance();
        assertNotNull(instance);
    }

    @Test
    void createLobby_createsLobbySuccessfullyWhenValidArgumentsProvided() {
        UUID userID = UUID.randomUUID();
        String lobbyName = "TestLobby";
        int totalPlayers = 4;
        User user = new User(userID, true);
        mc.getUsers().put(userID, user);

        mc.createLobby(userID, lobbyName, totalPlayers);

        assertNotNull(mc.getLobbyNotStarted());
        assertEquals(lobbyName + "'s lobby", mc.getLobbyNotStarted().getName());
        assertEquals(totalPlayers, mc.getLobbyNotStarted().getTotalPlayers());
        assertTrue(mc.getGameControllers().containsKey(mc.getLobbyNotStarted()));
    }

    @Test
    void createLobby_totalPlayersExceedsLimit() {
        UUID userID = UUID.randomUUID();
        String lobbyName = "TestLobby";
        int totalPlayers = 5;

        assertThrows(IllegalArgumentException.class, () -> mc.createLobby(userID, lobbyName, totalPlayers));
    }

    @Test
    void createLobby_associatesUserWithLobbySuccessfully() {
        UUID userID = UUID.randomUUID();
        String lobbyName = "TestLobby";
        int totalPlayers = 3;
        User user = new User(userID, false);
        mc.getUsers().put(userID, user);

        mc.createLobby(userID, lobbyName, totalPlayers);

        //TODO: Vedi metodo
        GameController gameController = mc.getGameControllers().get(mc.getLobbyNotStarted());
        assertNotNull(gameController);
        assertTrue(gameController.getUsers().contains(user));
    }

    @Test
    void joinLobby_addsUserToLobbySuccessfullyWhenLobbyExists() {
        UUID userID = UUID.randomUUID();
        User user = new User(userID, false);
        mc.getUsers().put(userID, user);
        mc.createLobby(userID, "TestLobby", 3);

        mc.joinLobby(userID);

        GameController gameController = mc.getGameControllers().get(mc.getLobbyNotStarted());
        assertNotNull(gameController);
        assertTrue(gameController.getUsers().contains(user));
    }

    @Test
    void joinLobby_sendsNoLobbyAvailableMessageWhenNoLobbyExists() throws NotBoundException, RemoteException {
        UUID userID = UUID.randomUUID();
        User user = new User(userID, false);
        mc.getUsers().put(userID, user);

        Connection connection = mc.getConnections().get(user);
        assertNotNull(connection);
        //assertTrue(connection.getSentMessages().stream()
        //        .anyMatch(message -> message.getType() == MessageType.NO_LOBBY_AVAILABLE));
    }

    /*@Test
    void sendEachConnection_sendsMessageToAllUsersInLobby() {
        LobbyInfo lobbyInfo = new LobbyInfo("TestLobby", 3);
        User user1 = new User(UUID.randomUUID(), false);
        User user2 = new User(UUID.randomUUID(), false);
        Connection connection1 = new Connection();
        Connection connection2 = new Connection();
        mc.getConnections().put(user1, connection1);
        mc.getConnections().put(user2, connection2);
        GameController gameController = new GameController();
        gameController.getUsers().add(user1);
        gameController.getUsers().add(user2);
        mc.getGameControllers().put(lobbyInfo, gameController);
        Message message = new ZeroArgMessage(MessageType.GAME_START);

        mc.sendEachConnection(lobbyInfo, message);

        assertTrue(connection1.getSentMessages().contains(message));
        assertTrue(connection2.getSentMessages().contains(message));
    }

    @Test
    void sendEachConnection_whenNoUsersInLobby() {
        LobbyInfo lobbyInfo = new LobbyInfo("EmptyLobby", 3);
        GameController gameController = new GameController();
        mc.getGameControllers().put(lobbyInfo, gameController);
        Message message = new ZeroArgMessage(MessageType.GAME_START);

        assertDoesNotThrow(() -> mc.sendEachConnection(lobbyInfo, message));
    }

    @Test
    void sendToConnection_sendsMessageWhenConnectionExists() {
        UUID userID = UUID.randomUUID();
        User user = new User(userID, false);
        Connection connection = new Connection();
        mc.getUsers().put(userID, user);
        mc.getConnections().put(user, connection);
        Message message = new ZeroArgMessage(MessageType.GAME_START);

        mc.sendToConnection(userID, message);

        assertTrue(connection.getSentMessages().contains(message));
    }


     */
    @Test
    void sendToConnection_whenConnectionDoesNotExist() {
        UUID userID = UUID.randomUUID();
        User user = new User(userID, false);
        mc.getUsers().put(userID, user);
        Message message = new ZeroArgMessage(MessageType.GAME_START);

        assertDoesNotThrow(() -> mc.sendToConnection(userID, message));
    }

    @Test
    void sendToConnection_whenUserDoesNotExist() {
        UUID userID = UUID.randomUUID();
        Message message = new ZeroArgMessage(MessageType.GAME_START);

        assertDoesNotThrow(() -> mc.sendToConnection(userID, message));
    }
}