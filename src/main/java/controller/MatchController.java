package controller;

import Model.Game.Board.Board;
import Model.Game.Lobby.LobbyInfo;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import event.Event;
import event.EventListener;
import event.NetworkTransceiver;
import event.game.UseCannons;
import event.game.UseEngine;
import event.lobby.CreateLobby;
import event.lobby.JoinLobby;
import event.lobby.LeaveLobby;
import event.lobby.SetNickname;
import network.Connection;
import network.User;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final NetworkTransceiver serverNetworkTransceiver;

    private final Map<String, LobbyInfo> lobbies;
    private final Map<LobbyInfo, NetworkTransceiver> networkTransceivers;
    private final Map<LobbyInfo, GameController> gameControllers;

    private Map<UUID, User> users;
    private Map<User, PlayerData> userPlayers;

    private EventListener<UseEngine> useEngineEventListener;
    private EventListener<UseCannons> useCannonEventListener;

    private MatchController(NetworkTransceiver serverNetworkTransceiver) {
        this.gameControllers = new HashMap<>();
        this.users = new HashMap<>();
        this.lobbies = new HashMap<>();
        this.networkTransceivers = new HashMap<>();
        this.serverNetworkTransceiver = serverNetworkTransceiver;

        EventListener<SetNickname> setNicknameEventListener = data -> {
            boolean nicknameAlreadyUsed = false;
            for (User user : users.values()) {
                if (user.getNickname().equals(data.nickname())) {
                    nicknameAlreadyUsed = true;
                    break;
                }
            }

            if (nicknameAlreadyUsed) {
                // TODO: error event, nickname already used
            } else {
                // TODO: create a new user
                // TODO: event that the user has been added
            }
        };

        EventListener<CreateLobby> createLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            User user = users.get(userID);
            LobbyInfo lobby = new LobbyInfo(user.getNickname(), data.maxPlayers(), data.level());
            lobbies.put(lobby.getName(), lobby);

            NetworkTransceiver networkTransceiver = new NetworkTransceiver();
            networkTransceiver.connect(user.getConnection());
            networkTransceivers.put(lobby, networkTransceiver);

            PlayerColor color = PlayerColor.BLUE;
            PlayerData player = new PlayerData(user.getNickname(), color, new SpaceShip(lobby.getLevel(), color));
            userPlayers.put(user, player);

            Board board;
            try {
                board = new Board(lobby.getLevel());
            } catch (IllegalArgumentException | JsonProcessingException e) {
                throw new IllegalStateException("Error creating board", e);
            }
            GameController gc = new GameController(board, lobby);
            gc.manageLobby(player, 1);
            gameControllers.put(lobby, gc);

            CreateLobby toSend = new CreateLobby(user.getNickname(), lobby.getName(), lobby.getTotalPlayers(), lobby.getLevel());
            serverNetworkTransceiver.broadcast(toSend);
            networkTransceivers.forEach((key, value) -> {
                value.broadcast(toSend);
            });
        };

        EventListener<JoinLobby> joinLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            LobbyInfo lobby = lobbies.get(data.lobbyID());

            if (lobby != null) {
                User user = users.get(userID);
                GameController gc = gameControllers.get(lobby);
                user.setLobby(lobby);

                PlayerColor[] colorsAlreadyUsed = userPlayers.entrySet().stream()
                        .filter(entry -> entry.getKey().getLobby() == lobby)
                        .map(entry -> entry.getValue().getColor())
                        .toArray(PlayerColor[]::new);
                PlayerColor color = PlayerColor.getFreeColor(colorsAlreadyUsed);

                PlayerData player = new PlayerData(user.getNickname(), color, new SpaceShip(lobby.getLevel(), color));
                userPlayers.put(user, player);
                gc.manageLobby(player, 0);

                Connection connection = user.getConnection();
                NetworkTransceiver networkTransceiver = networkTransceivers.get(lobby);
                networkTransceiver.connect(connection);
                this.serverNetworkTransceiver.disconnect(connection);

                serverNetworkTransceiver.broadcast(data);
                networkTransceivers.forEach((key, value) -> {
                    value.broadcast(data);
                });
            } else {
                // TODO: send error message to user if lobby is full
            }
        };

        EventListener<LeaveLobby> leaveLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            User user = users.get(userID);
            LobbyInfo lobby = user.getLobby();

            if (lobby != null) {
                if (user.getNickname().equals(lobby.getFounderNickname())) {
                    gameControllers.remove(lobby);
                    users.forEach((key, value) -> {
                        if (value.getLobby() != null && value.getLobby().equals(lobby)) {
                            value.setLobby(null);
                            userPlayers.remove(value);
                        }
                    });
                    networkTransceivers.remove(lobby);
                    // TODO: event that the lobby was removed
                } else {
                    GameController gc = gameControllers.get(lobby);
                    gc.manageLobby(userPlayers.get(user), 1);
                    user.setLobby(null);
                    userPlayers.remove(user);

                    Connection connection = user.getConnection();
                    this.serverNetworkTransceiver.connect(connection);
                    networkTransceivers.get(lobby).disconnect(connection);

                    serverNetworkTransceiver.broadcast(data);
                    networkTransceivers.forEach((key, value) -> {
                        value.broadcast(data);
                    });
                }
            } else {
                // TODO: send error message to user if lobby is not found
            }
        };

        this.serverNetworkTransceiver.registerListener(createLobbyEventListener);
        this.serverNetworkTransceiver.registerListener(joinLobbyEventListener);
        this.serverNetworkTransceiver.registerListener(leaveLobbyEventListener);

        /*
        this.useEngineEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            GameController gc = gameControllers.get(lobbies.get(userID));
            if (gc != null) {
                gc.useExtraStrength(userID, 0, data.enginesPowerToUse(), data.batteriesIDs());
            }
        };

        this.useCannonEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());;
            GameController gc = gameControllers.get(lobbies.get(userID));
            if (gc != null) {
                gc.useExtraStrength(userID, 1, data.cannonsPowerToUse(), data.batteriesIDs());
            }
        };

        this.serverNetworkTransceiver.registerListener(useEngineEventListener);
        this.serverNetworkTransceiver.registerListener(useCannonEventListener);
        */
    }

    public static void setup(NetworkTransceiver serverNetworkTransceiver) throws IllegalStateException {
        if (instance == null) {
            instance = new MatchController(serverNetworkTransceiver);
        } else {
            throw new IllegalStateException("MatchController is already initialized");
        }
    }

    public static MatchController getInstance() { return instance; }

    public void broadcast(LobbyInfo lobbyInfo, Event event) {
        NetworkTransceiver networkTransceiver = networkTransceivers.get(lobbyInfo);
        if (networkTransceiver != null) {
            networkTransceiver.broadcast(event);
        }
    }

}
