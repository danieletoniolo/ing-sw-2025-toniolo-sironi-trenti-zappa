package controller;

import Model.Game.Lobby.LobbyInfo;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final Map<LobbyInfo, GameController> gameControllers;
    // TODO: mappa <id, user>, fare controllo quando faccio il generateUUID che non sia già stato creato
    private final Set<String> users; // TODO: create a user class to manage users

    public MatchController() {
        gameControllers = new HashMap<>();
        users = Set.of();
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
        boolean found = false;

        for (LobbyInfo lobby : gameControllers.keySet()) {
            if (!lobby.isGameStarted()) {
                GameController gameController = gameControllers.get(lobby);
                if (gameController != null) {
                    gameController.joinGame(userID);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            // TODO: send to user that no lobby is available, so starting the process to create it
        }
    }

}
