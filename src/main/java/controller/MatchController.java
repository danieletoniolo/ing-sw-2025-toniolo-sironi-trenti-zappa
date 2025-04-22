package controller;

import Model.Game.Lobby.LobbyInfo;
import controller.event.game.GameEvents;
import controller.event.lobby.LobbyEvents;
import network.Connection;
import network.User;
import network.messages.Message;
import network.messages.MessageType;
import network.messages.SingleArgMessage;
import network.messages.ZeroArgMessage;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final Map<LobbyInfo, GameController> gameControllers;
    // TODO: mappa <id, user>, fare controllo quando faccio il generateUUID che non sia già stato creato
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

            // single user event
            gc.addEventHandler(userID, LobbyEvents.JOIN_LOBBY_SUCCESSFUL, (e) ->
                    sendToConnection(userID, new SingleArgMessage<>(MessageType.JOIN_LOBBY_SUCCESSFUL, e)));
            // lobby event
            gc.addEventHandler(userID, LobbyEvents.USER_JOINED_LOBBY, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USER_JOINED_LOBBY, e)));
            gc.addEventHandler(userID, LobbyEvents.USER_LEFT_LOBBY, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USER_LEFT_LOBBY, e)));
            // game event
            gc.addEventHandler(userID, GameEvents.GAME_START, (e) ->
                    sendEachConnection(lobbyNotStarted, new ZeroArgMessage(MessageType.GAME_START)));
            gc.addEventHandler(userID, GameEvents.GAME_END, (e) ->
                    sendEachConnection(lobbyNotStarted, new ZeroArgMessage(MessageType.GAME_END)));
            gc.addEventHandler(userID, GameEvents.USE_ENGINE, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USE_ENGINE, e)));
            gc.addEventHandler(userID, GameEvents.USE_CANNONS, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USE_CANNONS, e)));
            gc.addEventHandler(userID, GameEvents.REMOVE_CREW, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.REMOVE_CREW, e)));
            gc.addEventHandler(userID, GameEvents.SWAP_GOODS, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.SWAP_GOODS, e)));

        } else {
            sendToConnection(userID, new ZeroArgMessage(MessageType.NO_LOBBY_AVAILABLE));
        }

        if (lobbyNotStarted.canGameStart()) {
            gc.startGame();
            gc.executeHandlers(GameEvents.GAME_START, null);
            lobbyNotStarted = null;
        }
    }

    public void sendEachConnection(LobbyInfo lobbyInfo, Message message) {
        for (User user: gameControllers.get(lobbyInfo).getUsers()) {
            connections.get(user).send(message);
        }
    }

    public void sendToConnection(UUID userID, Message message) {
        Connection connection = connections.get(users.get(userID));
        if (connection != null) {
            connection.send(message);
        }
    }

}
