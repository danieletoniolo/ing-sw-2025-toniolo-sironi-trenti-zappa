package controller;

import Model.Game.Board.Board;
import Model.Game.Lobby.LobbyInfo;
import Model.Player.PlayerColor;
import Model.Player.PlayerData;
import Model.SpaceShip.SpaceShip;
import com.fasterxml.jackson.core.JsonProcessingException;
import event.eventType.Event;
import event.NetworkTransceiver;
import event.game.clientToServer.*;
import event.game.serverToClient.Pota;
import event.game.serverToClient.Success;
import event.lobby.clientToServer.*;
import event.lobby.serverToClient.*;
import network.User;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

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
     * The User is created only after the user has set the nickname. The UUID associated to the user is the same ID that is associated to the connection.
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
     * Registers all the lobby-related listeners to handle incoming network requests for creating, joining, leaving a lobby, and setting a nickname.
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
    }

    /**
     * Sets the nickname for a user. This method checks if the given nickname
     * is already in use. If the nickname is available, it assigns it to the user
     * and updates the user list. If the nickname is already used, it returns an
     * appropriate event indicating the failure.
     *
     * @param data an object containing the user ID and the desired nickname
     * @return an event indicating whether the nickname assignment was successful or not.
     */
    private Event setNickname(SetNickname data) {
        boolean nicknameAlreadyUsed = false;
        for (User user : users.values()) {
            if (user.getNickname().equals(data.nickname())) {
                nicknameAlreadyUsed = true;
                break;
            }
        }

        if (nicknameAlreadyUsed) {
            return new Pota(SetNickname.class, "Nickname already used");
        } else {
            UUID userID = UUID.fromString(data.userID());
            User user = new User(userID, data.nickname(), serverNetworkTransceiver.getConnection(userID));
            users.put(userID, user);

            // TODO: is it necessary to broadcast the nicknameSet to all the users?
            // Notify that a new user has joined the server
            NicknameSet nicknameSet = new NicknameSet(user.getNickname());
            serverNetworkTransceiver.broadcast(nicknameSet);

            // Send to the user the list of all the lobbies
            List<Pair<Integer, Integer>> lobbiesPlayers = lobbies.values().stream().map(lobbyInfo -> new Pair<>(lobbyInfo.getNumberOfPlayersEntered(), lobbyInfo.getTotalPlayers())).toList();
            Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), new ArrayList<>(lobbiesPlayers));
            serverNetworkTransceiver.send(userID, lobbiesEvent);
        }
        return new Success(SetNickname.class);
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
    private Event createLobby(CreateLobby data) {
        UUID userID = UUID.fromString(data.userID());
        User user = users.get(userID);

        // Creating the new lobby
        LobbyInfo lobby = new LobbyInfo(user.getNickname(), data.maxPlayers(), data.level());
        lobbies.put(lobby.getName(), lobby);

        // Trying to create the board
        Board board;
        try {
            board = new Board(lobby.getLevel());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            lobbies.remove(lobby.getName());
            return new Pota(CreateLobby.class, "Error creating board");
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
        PlayerData player = new PlayerData(user.getNickname(), color, new SpaceShip(lobby.getLevel(), color));
        userPlayers.put(user, player);
        userLobbyInfo.put(user, lobby);

        // Broadcast the information of the new player
        PlayerAdded playerAdded = new PlayerAdded(user.getNickname(), color.getValue());
        networkTransceiver.broadcast(playerAdded);

        // Creating the game controller
        GameController gc = new GameController(board, lobby);
        gc.manageLobby(player, 1);
        gameControllers.put(lobby, gc);

        // Notifying to all the clients that a new lobby has been created
        CreateLobby toSend = new CreateLobby(user.getNickname(), lobby.getName(), lobby.getTotalPlayers(), lobby.getLevel());
        serverNetworkTransceiver.broadcast(toSend);

        return new Success(CreateLobby.class);
    }

    /**
     * Handles the logic for a user leaving a lobby. If the user is the founder of the lobby,
     * additional cleanup operations are performed to remove the lobby and notify all clients
     * about the lobby removal. If the user is not the founder, they are simply removed from the
     * lobby and reconnected to the server's network transceiver.
     *
     * @param data an instance of {@code LeaveLobby} containing the information about the user
     *             leaving the lobby, such as the user ID.
     * @return an instance of {@code Event}, which could be a {@code Success} event if the operation
     *         is successful or a {@code Pota} event if the lobby is not found.
     */
    private Event leaveLobby(LeaveLobby data) {
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

                // Notify to all the old lobbies user of the lobbies list
                List<Pair<Integer, Integer>> lobbiesPlayers = lobbies.values().stream().map(lobbyInfo -> new Pair<>(lobbyInfo.getNumberOfPlayersEntered(), lobbyInfo.getTotalPlayers())).toList();
                Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), new ArrayList<>(lobbiesPlayers));

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
                List<Pair<Integer, Integer>> lobbiesPlayers = lobbies.values().stream().map(lobbyInfo -> new Pair<>(lobbyInfo.getNumberOfPlayersEntered(), lobbyInfo.getTotalPlayers())).toList();
                Lobbies lobbiesEvent = new Lobbies(new ArrayList<>(lobbies.keySet()), new ArrayList<>(lobbiesPlayers));
                serverNetworkTransceiver.send(user.getUUID(), lobbiesEvent);
            }
        } else {
            return new Pota(LeaveLobby.class, "Lobby not found");
        }

        return new Success(LeaveLobby.class);
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
    private Event joinLobby(JoinLobby data) {
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

            // Broadcast the information of the new player
            PlayerAdded playerAdded = new PlayerAdded(user.getNickname(), color != null ? color.getValue() : -1);
            networkTransceiver.broadcast(playerAdded);

            // Notifying to all the clients that a new user has joined the lobby
            LobbyJoined lobbyJoinedEvent = new LobbyJoined(user.getNickname(), lobby.getName());
            serverNetworkTransceiver.broadcast(lobbyJoinedEvent);
            networkTransceiver.broadcast(lobbyJoinedEvent);
        } else {
            return new Pota(JoinLobby.class, "Lobby is full");
        }
        return new Success(JoinLobby.class);
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the board.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event pickTileFromBoard(PickTileFromBoard data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 0, data.tileID());
            }
            return new Success(PickTileFromBoard.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PickTileFromBoard.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the reserve.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event pickTileFromReserve(PickTileFromReserve data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 1, data.tileID());
            }
            return new Success(PickTileFromReserve.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PickTileFromReserve.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that picks a tile from the spaceship.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event pickTileFromSpaceship(PickTileFromSpaceship data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.pickTile(player, 2, PLACEHOLDER);
            }
            return new Success(PickTileFromSpaceship.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PickTileFromSpaceship.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the board.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event placeTileToBoard(PlaceTileToBoard data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 0, PLACEHOLDER, PLACEHOLDER);
            }
            return new Success(PlaceTileToBoard.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PlaceTileToBoard.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the reserve.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event placeTileToReserve(PlaceTileToReserve data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 1, PLACEHOLDER, PLACEHOLDER);
            }
            return new Success(PlaceTileToReserve.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PlaceTileToReserve.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a tile on the spaceship.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event placeTileToSpaceship(PlaceTileToSpaceship data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeTile(player, 2, data.row(), data.column());
            }
            return new Success(PlaceTileToSpaceship.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PlaceTileToSpaceship.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses a deck.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event useDeck(PickLeaveDeck data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useDeck(player, data.usage(), data.deckIndex());
            }
            return new Success(PickLeaveDeck.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PickLeaveDeck.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that rotates a tile.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event rotateTile(RotateTile data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.rotateTile(player);
            }
            return new Success(RotateTile.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(RotateTile.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that flips the timer.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event flipTimer(FlipTimer data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.flipTimer(player);
            }
            return new Success(FlipTimer.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(FlipTimer.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that places a marker.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event placeMarker(PlaceMarker data) {
        UUID userID = UUID.fromString(data.userID());
        PlayerData player = userPlayers.get(users.get(userID));
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.placeMarker(player, data.position());
            }
            return new Success(PlaceMarker.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(PlaceMarker.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that manages a crew member.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event manageCrewMember(ManageCrewMember data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.manageCrewMember(userID, data.mode(), data.crewType(), data.cabinID());
            }
            return new Success(ManageCrewMember.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(ManageCrewMember.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses the engines.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event useEngines(UseEngines data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useExtraStrength(userID, 0, data.enginesIDs(), data.batteriesIDs());
            }
            return new Success(UseEngines.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(UseEngines.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that uses the cannons.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event useCannons(UseCannons data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.useExtraStrength(userID, 1, data.cannonsIDs(), data.batteriesIDs());
            }
            return new Success(UseCannons.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(UseCannons.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the penalty loss.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event setPenaltyLoss(SetPenaltyLoss data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setPenaltyLoss(userID, data.type(), data.penaltyLoss());
            }
            return new Success(SetPenaltyLoss.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(SetPenaltyLoss.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that selects a planet.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event selectPlanet(SelectPlanet data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.selectPlanet(userID, data.planetNumber());
            }
            return new Success(SelectPlanet.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(SelectPlanet.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the fragment choice.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event setFragmentChoice(ChooseFragment data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setFragmentChoice(userID, data.fragmentChoice());
            }
            return new Success(ChooseFragment.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(ChooseFragment.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the component to destroy.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event setComponentToDestroy(DestroyComponents data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setComponentToDestroy(userID, data.componentsToDestroy());
            }
            return new Success(DestroyComponents.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(DestroyComponents.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that rolls the dice.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event rollDice(RollDice data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.rollDice(userID);
            }
            return new Success(RollDice.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(RollDice.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the protect.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event setProtect(UseShield data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.setProtect(userID, data.batteryID());
            }
            return new Success(UseShield.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(UseShield.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that sets the goods to exchange.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event setGoodsToExchange(ExchangeGoods data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.exchangeGoods(userID, data.exchangeData());
            }
            return new Success(ExchangeGoods.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(ExchangeGoods.class, e.getMessage());
        }
    }

    /**
     * This method is used to handle the event of a user that swaps goods.
     * @param data is the event data that contains the information of the event.
     * @return     Return an event Success or Error depending on the result of the operation.
     *             This event is used to notify the client that the operation has been completed or not.
     */
    private Event swapGoods(SwapGoods data) {
        UUID userID = UUID.fromString(data.userID());
        LobbyInfo lobby = userLobbyInfo.get(users.get(userID));

        GameController gc = gameControllers.get(lobby);
        try {
            if (gc != null) {
                gc.swapGoods(userID, data.storageID1(), data.storageID2(), data.goods1to2(), data.goods2to1());
            }
            return new Success(SwapGoods.class);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new Pota(SwapGoods.class, e.getMessage());
        }
    }

    // TODO: giveUp event
}
