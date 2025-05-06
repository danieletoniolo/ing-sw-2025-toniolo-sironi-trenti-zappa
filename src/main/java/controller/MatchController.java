package controller;

import Model.Game.Lobby.LobbyInfo;
import controller.event.Event;
import controller.event.game.GameEvents;
import controller.event.lobby.LobbyEvents;
import network.Connection;
import network.User;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final Map<LobbyInfo, GameController> gameControllers;
    // TODO: MAP <id, user>, check if UUID is already created
    private Map<UUID, User> users;
    private Map<User, Connection> connections;
    private LobbyInfo lobbyNotStarted;

    public MatchController() {
        gameControllers = new HashMap<>();
        users = new HashMap<>();
        lobbyNotStarted = null;
    }

    public static MatchController getInstance() {
        if (instance == null) {
            instance = new MatchController();
        }
        return instance;
    }

    public void createLobby(UUID userID, String lobbyName, int totalPlayers) throws IllegalArgumentException {
        if (totalPlayers > 4) {
            throw new IllegalArgumentException("Total players cannot be greater than 4");
        }
        lobbyNotStarted = new LobbyInfo(lobbyName, totalPlayers);
        GameController gc = new GameController();
        gc.joinGame(users.get(userID), lobbyNotStarted);
        gameControllers.put(lobbyNotStarted, gc);
    }

    public void joinLobby(UUID userID) {
        GameController gc = gameControllers.get(lobbyNotStarted);
        if (lobbyNotStarted != null) {
            gc.joinGame(users.get(userID), lobbyNotStarted);
        } else {
            // TODO: SEND THAT NO LOBBY IS AVAILABLE
        }
    }

    public void sendEachConnection(LobbyInfo lobbyInfo, Event message) {
        for (User user: gameControllers.get(lobbyInfo).getUsers()) {
            connections.get(user).send(message);
        }
    }

    public void sendToConnection(UUID userID, Event message) {
        Connection connection = connections.get(users.get(userID));
        if (connection != null) {
            connection.send(message);
        }
    }

}
