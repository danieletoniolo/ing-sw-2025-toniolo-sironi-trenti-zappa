package it.polimi.ingsw.network;

import it.polimi.ingsw.model.game.lobby.LobbyInfo;

import java.util.UUID;

/**
 * User is created only when the client insert a valid nickname
 */
public class User {
    /** Unique identifier for the user */
    private UUID uuid;
    /** The nickname chosen by the user */
    private String nickname;
    /** The network connection associated with this user */
    private Connection connection;
    /** The lobby information where the user is currently participating, null if not in any lobby */
    private LobbyInfo lobby;

    /**
     * Creates a new User with the specified UUID, nickname, and connection.
     * The user is initially not associated with any lobby.
     *
     * @param uuid the unique identifier for the user
     * @param nickname the nickname chosen by the user
     * @param connection the network connection associated with this user
     */
    public User(UUID uuid, String nickname, Connection connection) {
        this.uuid = uuid;
        this.connection = connection;
        this.nickname = nickname;
        lobby = null;
    }

    /**
     * Sets the lobby information for this user.
     *
     * @param lobby the lobby information to associate with this user, or null to remove lobby association
     */
    public void setLobby(LobbyInfo lobby) {
        this.lobby = lobby;
    }

    /**
     * Gets the unique identifier for this user.
     *
     * @return the UUID of the user
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Gets the nickname of this user.
     *
     * @return the nickname chosen by the user
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the network connection associated with this user.
     *
     * @return the connection object for this user
     */
    public Connection getConnection() { return connection; }

    /**
     * Gets the lobby information where this user is currently participating.
     *
     * @return the lobby information, or null if the user is not in any lobby
     */
    public LobbyInfo getLobby() {
        return lobby;
    }
}
