package network;

import Model.Game.Lobby.LobbyInfo;

import java.util.UUID;

/**
 * User is created only when the client insert a valid nickname
 */
public class User {
    private UUID uuid;
    private String nickname;
    private Connection connection;
    private LobbyInfo lobby;

    public User(UUID uuid, String nickname, Connection connection) {
        this.uuid = uuid;
        this.connection = connection;
        this.nickname = nickname;
        lobby = null;
    }

    public void setLobby(LobbyInfo lobby) {
        this.lobby = lobby;
    }

    public UUID getUUID() {
        return uuid;
    }
    public String getNickname() {
        return nickname;
    }
    public Connection getConnection() { return connection; }
    public LobbyInfo getLobby() {
        return lobby;
    }
}
