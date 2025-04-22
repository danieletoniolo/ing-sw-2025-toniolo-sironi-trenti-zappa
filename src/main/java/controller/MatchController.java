package controller;

import Model.Game.Lobby.LobbyInfo;
import controller.event.EventType;
import controller.event.game.GameEvents;
import controller.event.game.UseCannons;
import controller.event.game.UseEngine;
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

    public void createLobby(UUID userID, String lobbyName, int totalPlayers) {
        LobbyInfo lobbyInfo = new LobbyInfo(lobbyName, totalPlayers);
        GameController gameController = new GameController();
        gameController.joinGame(userID);
        gameControllers.put(lobbyInfo, gameController);
    }

    public void joinLobby(UUID userID) {
        GameController gc = gameControllers.get(lobbyNotStarted);
        if (lobbyNotStarted != null) {
            gc.joinGame(userID);

            gc.addEventHandler(userID, GameEvents.GAME_START, (e) ->
                    sendEachConnection(lobbyNotStarted, new ZeroArgMessage(MessageType.GAME_START)));
            gc.addEventHandler(userID, GameEvents.USE_ENGINE, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USE_ENGINE, e)));
            gc.addEventHandler(userID, GameEvents.USE_CANNONS, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.USE_CANNONS, e)));
            gc.addEventHandler(userID, GameEvents.REMOVE_CREW, (e) ->
                    sendEachConnection(lobbyNotStarted, new SingleArgMessage<>(MessageType.REMOVE_CREW, e)));

        } else {
            // TODO: send to user that no lobby is available, so starting the process to create it
            // TODO: ADD LOBBY TO LOBBY NOT STARTED
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

}
