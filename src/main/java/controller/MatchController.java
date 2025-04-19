package controller;

import Model.Game.Lobby.LobbyInfo;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final Map<LobbyInfo, GameController> gameControllers;
    private LobbyInfo lobbyNotStarted;
    // TODO: mappa <id, user>, fare controllo quando faccio il generateUUID che non sia già stato creato
    private final ArrayList<String> users; // TODO: this will be a map of Lobby and ArrayList of connection

    public MatchController() {
        gameControllers = new HashMap<>();
        users = new ArrayList<>();
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
        if (lobbyNotStarted != null) {
            gameControllers.get(lobbyNotStarted).joinGame(userID);
        } else {
            // TODO: send to user that no lobby is available, so starting the process to create it
            // TODO: ADD LOBBY TO LOBBY NOT STARTED
        }

        if (lobbyNotStarted.canGameStart()) {
            // TODO: send to user that the game is starting
            gameControllers.get(lobbyNotStarted).startGame();
            lobbyNotStarted = null;
        }
    }

    public void sendEachConnection(LobbyInfo lobbyInfo) {
        // TODO: merge with network, for on the connection of that lobby
    }

}
