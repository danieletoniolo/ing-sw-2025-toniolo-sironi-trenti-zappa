package controller;

import Model.Game.Board.Board;
import Model.Game.Lobby.LobbyInfo;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import event.EventListener;
import event.NetworkTransceiver;
import event.game.PickTile;
import event.game.PlaceTile;
import event.game.UseCannons;
import event.game.UseEngines;
import event.lobby.*;
import network.Connection;
import network.User;

import java.util.*;

public class MatchController {
    private static MatchController instance;
    private final NetworkTransceiver serverNetworkTransceiver;

    private final Map<String, LobbyInfo> lobbies;
    private final Map<LobbyInfo, NetworkTransceiver> networkTransceivers;
    private final Map<LobbyInfo, GameController> gameControllers;

    private final Map<UUID, User> users;
    private final Map<User, PlayerData> userPlayers;
    private final Map<User, LobbyInfo> userLobbyInfo;

    private MatchController(NetworkTransceiver serverNetworkTransceiver) {
        this.gameControllers = new HashMap<>();
        this.users = new HashMap<>();
        this.userPlayers = new HashMap<>();
        this.userLobbyInfo = new HashMap<>();
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
                UUID userID = UUID.fromString(data.userID());
                User user = new User(userID, data.nickname(), serverNetworkTransceiver.getConnection(userID));
                users.put(userID, user);

                // TODO: event that the user has been added
                // TODO: synchronous event with the list of all the lobbies
            }
        };

        // TODO: this is an event that is not register on the serverNetworkTransceiver
        EventListener<LeaveLobby> leaveLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            User user = users.get(userID);
            LobbyInfo lobby = user.getLobby();

            if (lobby != null) {
                // Controlling if the user that is leaving is also the founder of the lobby
                if (user.getNickname().equals(lobby.getFounderNickname())) {
                    // If it is the founder, we need to remove the lobby
                    gameControllers.remove(lobby);
                    users.forEach((key, value) -> {
                        if (value.getLobby() != null && value.getLobby().equals(lobby)) {
                            value.setLobby(null);
                            userPlayers.remove(value);
                        }
                    });
                    userLobbyInfo.forEach((key, value) -> {
                        if (value.equals(lobby)) {
                            userLobbyInfo.remove(key);
                        }
                    });

                    // Notify to all the clients on the networkTransceiver of the lobby that the lobby has been removed
                    RemoveLobby removeLobbyEvent = new RemoveLobby(lobby.getName());
                    networkTransceivers.get(lobby).broadcast(removeLobbyEvent);
                    // TODO: synchronous event with the list of all the lobbies

                    // Removing the network transceiver of the lobby and attaching the users to the network transceiver of the server
                    networkTransceivers.remove(lobby);
                    for (User tempUser : users.values()) {
                        serverNetworkTransceiver.connect(tempUser.getUUID(), tempUser.getConnection());
                    }

                    // Notifying to all the clients that the lobby has been removed
                    serverNetworkTransceiver.broadcast(removeLobbyEvent);
                } else {
                    // Removing the user from the lobby
                    GameController gc = gameControllers.get(lobby);
                    gc.manageLobby(userPlayers.get(user), 1);
                    user.setLobby(null);
                    userPlayers.remove(user);
                    userLobbyInfo.remove(user);

                    // Attaching the user to the network transceiver of the server
                    serverNetworkTransceiver.connect(user.getUUID(), user.getConnection());
                    networkTransceivers.get(lobby).disconnect(user.getUUID());

                    // Notifying to all the clients that a user has left the lobby
                    serverNetworkTransceiver.broadcast(data);
                    networkTransceivers.get(lobby).broadcast(data);
                    // TODO: synchronous event with the list of all the lobbies for the user who left
                }
            } else {
                // TODO: send error message to user if lobby is not found
            }
        };

        EventListener<CreateLobby> createLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            User user = users.get(userID);

            // Creating the new lobby
            LobbyInfo lobby = new LobbyInfo(user.getNickname(), data.maxPlayers(), data.level());
            lobbies.put(lobby.getName(), lobby);

            // Creating the new network transceiver for the lobby and remove the current user from the serverNetworkTransceiver to the lobbyNetworkTransceiver
            NetworkTransceiver networkTransceiver = new NetworkTransceiver();
            networkTransceiver.registerListener(leaveLobbyEventListener);
            networkTransceiver.connect(user.getUUID(), user.getConnection());
            networkTransceivers.put(lobby, networkTransceiver);
            serverNetworkTransceiver.disconnect(userID);

            // Creating the playerData for the user
            PlayerColor color = PlayerColor.BLUE;
            PlayerData player = new PlayerData(user.getNickname(), color, new SpaceShip(lobby.getLevel(), color));
            userPlayers.put(user, player);
            userLobbyInfo.put(user, lobby);

            // Trying to create the board
            Board board;
            try {
                board = new Board(lobby.getLevel());
            } catch (IllegalArgumentException | JsonProcessingException e) {
                throw new IllegalStateException("Error creating board", e);
            }

            // Creating the game controller
            GameController gc = new GameController(board, lobby);
            gc.manageLobby(player, 1);
            gameControllers.put(lobby, gc);

            // Notifying to all the clients that a new lobby has been created
            CreateLobby toSend = new CreateLobby(user.getNickname(), lobby.getName(), lobby.getTotalPlayers(), lobby.getLevel());
            serverNetworkTransceiver.broadcast(toSend);
            /*
            networkTransceivers.forEach((key, value) -> {
                value.broadcast(toSend);
            });
             */
        };

        EventListener<JoinLobby> joinLobbyEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            LobbyInfo lobby = lobbies.get(data.lobbyID());

            if (lobby != null) {
                User user = users.get(userID);
                GameController gc = gameControllers.get(lobby);
                user.setLobby(lobby);

                // Choosing the color for the user in the lobby
                PlayerColor[] colorsAlreadyUsed = userPlayers.entrySet().stream()
                        .filter(entry -> entry.getKey().getLobby() == lobby)
                        .map(entry -> entry.getValue().getColor())
                        .toArray(PlayerColor[]::new);
                PlayerColor color = PlayerColor.getFreeColor(colorsAlreadyUsed);

                // Creating the playerData for the user
                PlayerData player = new PlayerData(user.getNickname(), color, new SpaceShip(lobby.getLevel(), color));
                userPlayers.put(user, player);
                userLobbyInfo.put(user, lobby);
                gc.manageLobby(player, 0);

                // Attaching the user to the network transceiver of the lobby
                NetworkTransceiver networkTransceiver = networkTransceivers.get(lobby);
                networkTransceiver.connect(user.getUUID(), user.getConnection());
                serverNetworkTransceiver.disconnect(user.getUUID());

                // Notifying to all the clients that a new user has joined the lobby
                serverNetworkTransceiver.broadcast(data);
                networkTransceiver.broadcast(data);
            } else {
                // TODO: send error message to user if lobby is full
            }
        };

        this.serverNetworkTransceiver.registerListener(setNicknameEventListener);
        this.serverNetworkTransceiver.registerListener(createLobbyEventListener);
        this.serverNetworkTransceiver.registerListener(joinLobbyEventListener);

        // TODO: these events have to be registered in order to be used with the requester / responder pattern
        EventListener<PickTile> pickTileEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            PlayerData player = userPlayers.get(users.get(userID));
            LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

            GameController gc = gameControllers.get(lobby);
            if (gc != null) {
                gc.pickTile(player, data.fromWhere(), data.tileID());
            }
        };

        EventListener<PlaceTile> placeTileEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            PlayerData player = userPlayers.get(users.get(userID));
            LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

            GameController gc = gameControllers.get(lobby);
            if (gc != null) {
                gc.placeTile(player, data.fromWhere(), data.row(), data.col());
            }
        };

        EventListener<UseEngines> useEnginesEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

            GameController gc = gameControllers.get(lobby);
            if (gc != null) {
                gc.useExtraStrength(userID, 0, data.enginesPowerToUse(), data.batteriesIDs());
            }
        };

        EventListener<UseCannons> useCannonEventListener = data -> {
            UUID userID = UUID.fromString(data.userID());
            LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

            GameController gc = gameControllers.get(lobby);
            if (gc != null) {
                gc.useExtraStrength(userID, 1, data.cannonsPowerToUse(), data.batteriesIDs());
            }
        };


    }

    public static void setup(NetworkTransceiver serverNetworkTransceiver) throws IllegalStateException {
        if (instance == null) {
            instance = new MatchController(serverNetworkTransceiver);
        } else {
            throw new IllegalStateException("MatchController is already initialized");
        }
    }

    public static MatchController getInstance() { return instance; }

    public NetworkTransceiver getNetworkTransceiver(LobbyInfo lobbyInfo) throws IllegalStateException {
        NetworkTransceiver networkTransceiver = networkTransceivers.get(lobbyInfo);
        if (networkTransceiver == null) {
            throw new IllegalStateException("There is no NetworkTransceiver for this lobby");
        }

        return networkTransceiver;
    }
}
