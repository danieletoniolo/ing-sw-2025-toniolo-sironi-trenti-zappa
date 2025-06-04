package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.TransmitterEventWrapper;
import it.polimi.ingsw.event.game.clientToServer.planets.SelectPlanet;
import it.polimi.ingsw.event.game.clientToServer.deck.PickLeaveDeck;
import it.polimi.ingsw.event.game.clientToServer.dice.RollDice;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseCannons;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseEngines;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.clientToServer.goods.ExchangeGoods;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromBoard;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromReserve;
import it.polimi.ingsw.event.game.clientToServer.pickTile.PickTileFromSpaceship;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToBoard;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToReserve;
import it.polimi.ingsw.event.game.clientToServer.placeTile.PlaceTileToSpaceship;
import it.polimi.ingsw.event.game.clientToServer.player.*;
import it.polimi.ingsw.event.game.clientToServer.rotateTile.RotateTile;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.clientToServer.timer.FlipTimer;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.model.game.board.Board;
import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.model.good.Good;
import it.polimi.ingsw.model.good.GoodType;
import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.spaceship.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.polimi.ingsw.event.NetworkTransceiver;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.game.serverToClient.status.Tac;
import it.polimi.ingsw.event.lobby.clientToServer.*;
import it.polimi.ingsw.event.lobby.serverToClient.*;
import it.polimi.ingsw.network.User;
import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.time.LocalDateTime;
import java.util.*;

/**
 * MatchController is the main controller of the game. It manages the lobbies, the users and the game controllers.
 * It is a singleton class, so it can be accessed from anywhere in the code.
 * It also manages the network transceivers for each lobby and the handle of the events that the server receive from the clients.
 * The MatchController is associated also with a serverNetworkTransceiver, which is used to communicate with the clients when they are not in a lobby.
 */
public class MatchController {
    private static MatchController instance;

    /**
     * Map of all the lobbies created in the game. The key is the name of the lobby and the value is the LobbyInfo object.
     */
    private final Map<String, LobbyInfo> lobbies;

    /**
     * Represents the server's network transceiver responsible for handling communication processes such as sending and receiving data over the network.
     */
    private final NetworkTransceiver serverNetworkTransceiver;

    /**
     * Map of all the network transceivers created in the game. The key is the LobbyInfo object and the value is the NetworkTransceiver object.
     */
    private final Map<LobbyInfo, NetworkTransceiver> networkTransceivers;

    /**
     * Map of all the game controllers. The key is the LobbyInfo object and the value is the GameController object.
     */
    private final Map<LobbyInfo, GameController> gameControllers;

    /**
     * Map of all the users connected to the server. The key is the UUID of the user and the value is the User object.
     * The User is created only after the user has set the userID. The UUID associated to the user is the same ID that is associated to the connection.
     */
    private final Map<UUID, User> users;

    /**
     * Map of all the players in the game. The key is the User object and the value is the PlayerData object.
     * The PlayerData is created only after the user has created or joined a lobby.
     */
    private final Map<User, PlayerData> userPlayers;

    /**
     * Map of all the lobbies associated to the users. The key is the User object and the value is the LobbyInfo object.
     */
    private final Map<User, LobbyInfo> userLobbyInfo;

    static private final Integer PLACEHOLDER = -1;

    private MatchController(NetworkTransceiver serverNetworkTransceiver) {
        this.gameControllers = new HashMap<>();
        this.users = new HashMap<>();
        this.userPlayers = new HashMap<>();
        this.userLobbyInfo = new HashMap<>();
        this.lobbies = new HashMap<>();
        this.networkTransceivers = new HashMap<>();
        this.serverNetworkTransceiver = serverNetworkTransceiver;
        this.registerAllLobbyListeners();
    }

    /**
     * This method is used to initialize the MatchController. It is a singleton class, so it can be accessed from anywhere in the code.
     * It is called by the Server class when the server is started.
     * @param serverNetworkTransceiver is the network transceiver used to communicate with the clients when they are not in a lobby.
     * @throws IllegalStateException   if the MatchController is already initialized
     */
    public static void setUp(NetworkTransceiver serverNetworkTransceiver) throws IllegalStateException {
        if (instance == null) {
            instance = new MatchController(serverNetworkTransceiver);
        } else {
            throw new IllegalStateException("MatchController is already initialized");
        }
    }

    /**
     * This method is used to get the instance of the MatchController. It is a singleton class, so it can be accessed from anywhere in the code.
     * @return the instance of the MatchController
     */
    public static MatchController getInstance() { return instance; }

    /**
     * This method is used to get the list of the network transceivers associated to a lobby.
     * @param lobbyInfo              is the lobby info object that contains the information of the lobby.
     * @return                       the network transceiver associated to the lobby
     * @throws IllegalStateException if there is no network transceiver for the lobby
     */
    public NetworkTransceiver getNetworkTransceiver(LobbyInfo lobbyInfo) throws IllegalStateException {
        NetworkTransceiver networkTransceiver = networkTransceivers.get(lobbyInfo);
        if (networkTransceiver == null) {
            throw new IllegalStateException("There is no NetworkTransceiver for this lobby");
        }

        return networkTransceiver;
    }

    /**
     * Registers all the lobby-related listeners to handle incoming network requests for creating, joining, leaving a lobby, and setting a userID.
     * Associates each request handler with the server network transceiver and the respective responder method.
     */
    private void registerAllLobbyListeners() {
        CreateLobby.responder(serverNetworkTransceiver, this::createLobby);
        JoinLobby.responder(serverNetworkTransceiver, this::joinLobby);
        SetNickname.responder(serverNetworkTransceiver, this::setNickname);
    }

    /**
     * Registers all game-related event listeners for network communication. Each listener corresponds
     * to a specific game-related event, and responds to incoming data by invoking the appropriate
     * method to handle the event. This method is integral to ensuring proper interaction between
     * the server and clients during a game session.
     *
     * @param networkTransceiver the network transceiver used to handle communication with clients for
     *                           receiving and responding to game-related events.
     */
    private void registerAllGameListeners(NetworkTransceiver networkTransceiver) {
        PickTileFromBoard.responder(networkTransceiver, this::pickTileFromBoard);
        PickTileFromReserve.responder(networkTransceiver, this::pickTileFromReserve);
        PickTileFromSpaceship.responder(networkTransceiver, this::pickTileFromSpaceship);
        PlaceTileToBoard.responder(networkTransceiver, this::placeTileToBoard);
        PlaceTileToReserve.responder(networkTransceiver, this::placeTileToReserve);
        PlaceTileToSpaceship.responder(networkTransceiver, this::placeTileToSpaceship);
        PickLeaveDeck.responder(networkTransceiver, this::useDeck);
        RotateTile.responder(networkTransceiver, this::rotateTile);
        FlipTimer.responder(networkTransceiver, this::flipTimer);
        PlaceMarker.responder(networkTransceiver, this::placeMarker);
        ManageCrewMember.responder(networkTransceiver, this::manageCrewMember);
        UseEngines.responder(networkTransceiver, this::useEngines);
        UseCannons.responder(networkTransceiver, this::useCannons);
        SetPenaltyLoss.responder(networkTransceiver, this::setPenaltyLoss);
        SelectPlanet.responder(networkTransceiver, this::selectPlanet);
        ChooseFragment.responder(networkTransceiver, this::setFragmentChoice);
        DestroyComponents.responder(networkTransceiver, this::setComponentToDestroy);
        RollDice.responder(networkTransceiver, this::rollDice);
        UseShield.responder(networkTransceiver, this::setProtect);
        ExchangeGoods.responder(networkTransceiver, this::setGoodsToExchange);
        SwapGoods.responder(networkTransceiver, this::swapGoods);
        PlayerReady.responder(networkTransceiver, this::playerReady);
        Play.responder(networkTransceiver, this::play);
        EndTurn.responder(networkTransceiver, this::endTurn);
        GiveUp.responder(networkTransceiver, this::giveUp);
    }

    /**
     * Sets the userID for a user. This method checks if the given userID
     * is already in use. If the userID is available, it assigns it to the user
     * and updates the user list. If the userID is already used, it returns an
     * appropriate event indicating the failure.
     *
     * @param data an object containing the user ID and the desired userID
     * @return an event indicating whether the userID assignment was successful or not.
     */
    private StatusEvent setNickname(SetNickname data) {
        boolean nicknameAlreadyUsed = false;
        if (data.nickname() == null || data.nickname().isEmpty()) {
            Logger.getInstance().logWarning("User " + data.userID() + " tried to set an null or empty nickname", false);
            return new Pota(data.userID(), SetNickname.class, "Nickname cannot be null or empty");
        }
        for (User user : users.values()) {
            if (user.getNickname().equals(data.nickname())) {
                nicknameAlreadyUsed = true;
                break;
            }
        }

        if (nicknameAlreadyUsed) {
            Logger.getInstance().logWarning("User " + data.userID() + " tried to set a nickname already used: " + data.nickname(), false);
            return new Pota(data.userID(), SetNickname.class, "Nickname already used");
        } else {
            UUID userID = UUID.fromString(data.userID());
            User user = new User(userID, data.nickname(), serverNetworkTransceiver.getConnection(userID));
            users.put(userID, user);

            // Notify that a new user has joined the server
            NicknameSet nicknameSet = new NicknameSet(user.getNickname());
            serverNetworkTransceiver.send(userID, nicknameSet);

            // Send to the user the list of all the lobbies
            List<Pair<Integer, Integer>> lobbiesPlayers = new ArrayList<>();
            List<Integer> lobbiesLevels = new ArrayList<>();
            for (LobbyInfo lobby : lobbies.values()) {
                lobbiesPlayers.add(new Pair<>(lobby.getNumberOfPlayersEntered(), lobby.getTotalPlayers()));
                lobbiesLevels.add(lobby.getLevel().getValue());
            }
            Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), lobbiesPlayers, lobbiesLevels);
            serverNetworkTransceiver.send(userID, lobbiesEvent);
            Logger.getInstance().logInfo("User " + data.userID() + " set nickname: " + data.nickname(), false);
        }
        return new Tac(data.userID(), SetNickname.class);
    }

    /**
     * Creates a new game lobby and initializes all necessary components for the lobby,
     * including the lobby details, network transceivers, player data, game board, and game controller.
     * Additionally, broadcasts the new lobby creation details to all connected clients.
     *
     * @param data a {@code CreateLobby} object containing the user ID of the host, maximum number
     *             of players, and the level for the new lobby.
     * @return an {@code Event} indicating the successful creation of the lobby.
     * @throws IllegalStateException if there is an error while creating the game board.
     */
    private TransmitterEventWrapper createLobby(CreateLobby data) {
        UUID userID = UUID.fromString(data.userID());
        User user = users.get(userID);

        // Creating the new lobby
        LobbyInfo lobby = new LobbyInfo(user.getNickname(), data.maxPlayers(), Level.fromInt(data.level()));
        lobbies.put(lobby.getName(), lobby);

        // Trying to create the board
        Board board;
        try {
            board = new Board(lobby.getLevel());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            lobbies.remove(lobby.getName());
            return new TransmitterEventWrapper(serverNetworkTransceiver, new Pota(data.userID(), CreateLobby.class, "Error creating the board"));
        }

        // Creating the new network transceiver for the lobby
        NetworkTransceiver networkTransceiver = new NetworkTransceiver();

        // Registering the listeners for the leave of a lobby and registering all game listeners
        LeaveLobby.responder(networkTransceiver, this::leaveLobby);
        registerAllGameListeners(networkTransceiver);

        // Remove the current user from the serverNetworkTransceiver to the lobbyNetworkTransceiver
        networkTransceiver.connect(user.getUUID(), user.getConnection());
        networkTransceivers.put(lobby, networkTransceiver);
        serverNetworkTransceiver.disconnect(userID);

        // Creating the playerData for the user
        PlayerColor color = PlayerColor.BLUE;
        PlayerData player = new PlayerData(user.getNickname(), data.userID(), color, new SpaceShip(lobby.getLevel(), color));
        userPlayers.put(user, player);
        userLobbyInfo.put(user, lobby);

        // Creating the game controller
        GameController gc = new GameController(board, lobby);
        gc.manageLobby(player, 1);
        gameControllers.put(lobby, gc);

        // Linking the user to the lobby
        user.setLobby(lobby);

        // Notifying to all the clients that a new lobby has been created
        LobbyCreated toSend = new LobbyCreated(user.getNickname(), lobby.getName(), lobby.getTotalPlayers(), lobby.getLevel().getValue());
        serverNetworkTransceiver.broadcast(toSend);
        networkTransceiver.broadcast(toSend);

        // Broadcast the information of the new player
        PlayerAdded playerAdded = new PlayerAdded(user.getNickname(), color.getValue());
        networkTransceiver.broadcast(playerAdded);

        Logger.getInstance().logInfo("User " + data.userID() + " created lobby: " + lobby.getName(), false);

        return new TransmitterEventWrapper(networkTransceiver, new Tac(data.userID(), CreateLobby.class));
    }

    /**
     * Handles the process of a user leaving a lobby. If the user leaving is the founder
     * of the lobby, the lobby will be completely removed, and all users in the lobby
     * will be reconnected to the server's main network transceiver. If the user is
     * not the founder, they are simply removed from the lobby and reconnected to
     * the server. Relevant notifications are sent to clients when a lobby is removed
     * or a user leaves.
     *
     * @param data an object containing the information about the user who is leaving the lobby
     * @return an Event indicating the result of the leave operation. If the lobby is not found,
     *         returns a failure event. If successful, returns a success event.
     */
    private TransmitterEventWrapper leaveLobby(LeaveLobby data) {
        UUID userID = UUID.fromString(data.userID());
        User user = users.get(userID);
        LobbyInfo lobby = user.getLobby();

        if (lobby != null) {
            // Controlling if the user that is leaving is also the founder of the lobby
            if (user.getNickname().equals(lobby.getFounderNickname())) {
                // If it is the founder, we need to remove the lobby
                gameControllers.remove(lobby);
                users.forEach((_, value) -> {
                    if (value.getLobby() != null && value.getLobby().equals(lobby)) {
                        value.setLobby(null);
                        userPlayers.remove(value);
                    }
                });
                List<User> toRemove = new ArrayList<>();
                for (User u : userLobbyInfo.keySet()) {
                    if (userLobbyInfo.get(u).equals(lobby)) {
                        toRemove.add(u);
                    }
                }
                for (User u : toRemove) {
                    userLobbyInfo.remove(u);
                }

                // Notify to all the clients on the networkTransceiver of the lobby that the lobby has been removed
                LobbyRemoved removeLobbyEvent = new LobbyRemoved(lobby.getName());
                networkTransceivers.get(lobby).broadcast(removeLobbyEvent);

                // Notify to all the old lobbies user of the lobbies list
                List<Pair<Integer, Integer>> lobbiesPlayers = new ArrayList<>();
                List<Integer> lobbiesLevels = new ArrayList<>();
                for (LobbyInfo lobbyTemp : lobbies.values()) {
                    lobbiesPlayers.add(new Pair<>(lobbyTemp.getNumberOfPlayersEntered(), lobbyTemp.getTotalPlayers()));
                    lobbiesLevels.add(lobbyTemp.getLevel().getValue());
                }
                Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), lobbiesPlayers, lobbiesLevels);

                // Removing the network transceiver of the lobby and attaching the users to the network transceiver of the server
                networkTransceivers.remove(lobby);
                for (User tempUser : users.values()) {
                    serverNetworkTransceiver.connect(tempUser.getUUID(), tempUser.getConnection());
                    serverNetworkTransceiver.send(tempUser.getUUID(), lobbiesEvent);
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
                LobbyLeft lobbyLeftEvent = new LobbyLeft(user.getNickname(), lobby.getName());
                serverNetworkTransceiver.broadcast(lobbyLeftEvent);
                networkTransceivers.get(lobby).broadcast(lobbyLeftEvent);

                // Notify the single user of the lobby list
                List<Pair<Integer, Integer>> lobbiesPlayers = new ArrayList<>();
                List<Integer> lobbiesLevels = new ArrayList<>();
                for (LobbyInfo lobbyTemp : lobbies.values()) {
                    lobbiesPlayers.add(new Pair<>(lobbyTemp.getNumberOfPlayersEntered(), lobbyTemp.getTotalPlayers()));
                    lobbiesLevels.add(lobbyTemp.getLevel().getValue());
                }
                Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), lobbiesPlayers, lobbiesLevels);
                serverNetworkTransceiver.send(user.getUUID(), lobbiesEvent);
            }
        } else {
            Logger.getInstance().logWarning("User " + data.userID() + " tried to leave a lobby that does not exist", false);
            return new TransmitterEventWrapper(serverNetworkTransceiver, new Pota(data.userID(), LeaveLobby.class, "Lobby not found"));
        }

        Logger.getInstance().logInfo("Game " + lobby.getName() + ": User " + data.userID() + " left the lobby", false);
        return new TransmitterEventWrapper(serverNetworkTransceiver, new Tac(data.userID(), LeaveLobby.class));
    }

    /**
     * Handles the process of a user joining a game lobby. This method assigns the user
     * to the specified lobby, assigns a unique player color, updates the necessary
     * mappings for user and lobby relationships, manages the lobby state, and
     * updates network connections accordingly.
     *
     * @param data An object containing the details of the join lobby request,
     *             including user ID and lobby ID.
     * @return An Event object indicating the result of the operation. If the user
     *         successfully joins the lobby, a Success Event is returned. If the lobby
     *         is full or another issue occurs, an error Event is returned.
     */
    private TransmitterEventWrapper joinLobby(JoinLobby data) {
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
            PlayerData player = new PlayerData(user.getNickname(), data.userID(), color, new SpaceShip(lobby.getLevel(), color));
            userPlayers.put(user, player);
            userLobbyInfo.put(user, lobby);
            gc.manageLobby(player, 0);

            // Attaching the user to the network transceiver of the lobby
            NetworkTransceiver networkTransceiver = networkTransceivers.get(lobby);
            networkTransceiver.connect(user.getUUID(), user.getConnection());
            serverNetworkTransceiver.disconnect(user.getUUID());

            // Broadcast the information of the new player
            PlayerAdded playerAdded = new PlayerAdded(user.getNickname(), color != null ? color.getValue() : -1);
            networkTransceiver.broadcast(playerAdded);

            // TODO: SEND LIST OF PLAYERDATA IN THE LOBBY TO THE CLIENT

            // Notifying to all the clients that a new user has joined the lobby
            LobbyJoined lobbyJoinedEvent = new LobbyJoined(user.getNickname(), lobby.getName());
            serverNetworkTransceiver.broadcast(lobbyJoinedEvent);
            networkTransceiver.broadcast(lobbyJoinedEvent);

            return new TransmitterEventWrapper(networkTransceiver, new Tac(data.userID(), JoinLobby.class));
        }
        return new TransmitterEventWrapper(serverNetworkTransceiver, new Pota(data.userID(), JoinLobby.class, "Lobby is full"));
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the board.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent pickTileFromBoard(PickTileFromBoard data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 0, data.tileID());
            }
            return new Tac(data.userID(), PickTileFromBoard.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PickTileFromBoard.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the reserve.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent pickTileFromReserve(PickTileFromReserve data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 1, data.tileID());
            }
            return new Tac(data.userID(), PickTileFromReserve.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PickTileFromReserve.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the spaceship.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent pickTileFromSpaceship(PickTileFromSpaceship data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 2, PLACEHOLDER);
            }
            return new Tac(data.userID(), PickTileFromSpaceship.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PickTileFromSpaceship.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the board.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent placeTileToBoard(PlaceTileToBoard data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 0, PLACEHOLDER, PLACEHOLDER);
            }
            return new Tac(data.userID(), PlaceTileToBoard.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PlaceTileToBoard.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the reserve.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent placeTileToReserve(PlaceTileToReserve data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 1, PLACEHOLDER, PLACEHOLDER);
            }
            return new Tac(data.userID(), PlaceTileToReserve.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PlaceTileToReserve.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the spaceship.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent placeTileToSpaceship(PlaceTileToSpaceship data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 2, data.row(), data.column());
            }
            return new Tac(data.userID(), PlaceTileToSpaceship.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PlaceTileToSpaceship.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses a deck.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent useDeck(PickLeaveDeck data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useDeck(player, data.usage(), data.deckIndex());
            }
            return new Tac(data.userID(), PickLeaveDeck.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PickLeaveDeck.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that rotates a tile.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent rotateTile(RotateTile data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.rotateTile(player);
            }
            return new Tac(data.userID(), RotateTile.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), RotateTile.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that flips the timer.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent flipTimer(FlipTimer data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.flipTimer(player);
            }
            return new Tac(data.userID(), FlipTimer.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), FlipTimer.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a marker.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent placeMarker(PlaceMarker data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeMarker(player, data.position());
            }
            return new Tac(data.userID(), PlaceMarker.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), PlaceMarker.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that manages a crew member.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent manageCrewMember(ManageCrewMember data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.manageCrewMember(userID, data.mode(), data.crewType(), data.cabinID());
            }
            return new Tac(data.userID(), ManageCrewMember.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), ManageCrewMember.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses the engines.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent useEngines(UseEngines data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useExtraStrength(userID, 0, data.enginesIDs(), data.batteriesIDs());
            }
            return new Tac(data.userID(), UseEngines.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), UseEngines.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses the cannons.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent useCannons(UseCannons data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useExtraStrength(userID, 1, data.cannonsIDs(), data.batteriesIDs());
            }
            return new Tac(data.userID(), UseCannons.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), UseCannons.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the penalty loss.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent setPenaltyLoss(SetPenaltyLoss data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setPenaltyLoss(userID, data.type(), data.penaltyLoss());
            }
            return new Tac(data.userID(), SetPenaltyLoss.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), SetPenaltyLoss.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that selects a planet.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent selectPlanet(SelectPlanet data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.selectPlanet(userID, data.planetNumber());
            }
            return new Tac(data.userID(), SelectPlanet.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), SelectPlanet.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the fragment choice.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent setFragmentChoice(ChooseFragment data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setFragmentChoice(userID, data.fragmentChoice());
            }
            return new Tac(data.userID(), ChooseFragment.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), ChooseFragment.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the component to destroy.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent setComponentToDestroy(DestroyComponents data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setComponentToDestroy(userID, data.componentsToDestroy());
            }
            return new Tac(data.userID(), DestroyComponents.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), DestroyComponents.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that rolls the dice.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent rollDice(RollDice data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.rollDice(userID);
            }
            return new Tac(data.userID(), RollDice.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), RollDice.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the protect.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent setProtect(UseShield data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setProtect(userID, data.batteryID());
            }
            return new Tac(data.userID(), UseShield.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), UseShield.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the goods to exchange.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent setGoodsToExchange(ExchangeGoods data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                List<Triplet<List<Good>, List<Good>, Integer>> convertedData = data.exchangeData().stream()
                        .map(t -> new Triplet<>(
                                t.getValue0().stream()
                                    .map(GoodType::fromInt)
                                    .map(Good::new)
                                    .toList(),
                                t.getValue1().stream()
                                    .map(GoodType::fromInt)
                                    .map(Good::new)
                                    .toList(),
                                t.getValue2()))
                        .toList();
                gc.exchangeGoods(userID, convertedData);
            }
            return new Tac(data.userID(), ExchangeGoods.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), ExchangeGoods.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that swaps goods.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent swapGoods(SwapGoods data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.swapGoods(userID, data.storageID1(), data.storageID2(), data.goods1to2().stream().map(t -> new Good(GoodType.fromInt(t))).toList(), data.goods2to1().stream().map(t -> new Good(GoodType.fromInt(t))).toList());
            }
            return new Tac(data.userID(), SwapGoods.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), SwapGoods.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the player isReady.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private StatusEvent playerReady(PlayerReady data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (data.isReady()) {
                lobby.addPlayerReady(userID);
            } else {
                lobby.removePlayerReady(userID);
            }

            if (lobby.canGameStart()) {
                LobbyRemoved removeLobbyEvent = new LobbyRemoved(lobby.getName());
                serverNetworkTransceiver.broadcast(removeLobbyEvent);

                int timerDuration = 5000;
                StartingGame startingGameEvent = new StartingGame(LocalDateTime.now().toString(), timerDuration);
                networkTransceivers.get(lobby).broadcast(startingGameEvent);
                (new Timer()).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gc.startGame();
                    }
                }, timerDuration);
            }
            Logger.getInstance().logInfo("Game " + lobby.getName() + ": user " + data.userID() + " is ready: " + data.isReady(), false);
            return new Tac(data.userID(), PlayerReady.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Logger.getInstance().logWarning("Game " + lobby.getName() + ": user " + data.userID() + " try to set ready to " + data.isReady() + " but an error occurred: " + e.getMessage(), false);
            return new Pota(data.userID(), PlayerReady.class, e.getMessage());
        }
    }

    /**
     * Executes the play action for a given user using the provided play data.
     * It retrieves user-specific information, determines the associated game controller,
     * and performs the play action if the game controller is available.
     *
     * @param data The play data containing the user ID and relevant play details.
     * @return     An Event object representing the result of the play action. Returns a Tac object
     *             if the play action is successful, or a Pota object if an exception occurs.
     */
    private StatusEvent play(Play data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.play(player);
            }
            return new Tac(data.userID(), Play.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), Play.class, e.getMessage());
        }
    }

    /**
     * Handles the event of a user ending their turn in the game.
     * It retrieves the user ID from the data, finds the corresponding lobby,
     * and calls the endTurn method on the game controller associated with that lobby.
     *
     * @param data The EndTurn event data containing the user ID.
     * @return An Event object indicating the result of the operation.
     */
    private StatusEvent endTurn(EndTurn data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.endTurn(userID);
            }
            return new Tac(data.userID(), EndTurn.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), EndTurn.class, e.getMessage());
        }
    }

    /**
     * Handles the event of a user giving up in the game.
     * It retrieves the user ID from the data, finds the corresponding lobby,
     * and calls the giveUp method on the game controller associated with that lobby.
     *
     * @param data The GiveUp event data containing the user ID.
     * @return An Event object indicating the result of the operation.
     */
    private StatusEvent giveUp(GiveUp data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.giveUp(userID);
            }

            return new Tac(data.userID(), GiveUp.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(data.userID(), GiveUp.class, e.getMessage());
        }
    }
}
