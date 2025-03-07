package Model.Game.Lobby;

import Model.Player.PlayerData;

import java.util.ArrayList;

public class LobbyInfo {
    private final ArrayList<PlayerData> player;
    private String name;
    private final int totalPlayers;
    private int numberOfPlayersEntered;

    LobbyInfo(String name, int totalPlayers) {
        this.numberOfPlayersEntered = 0;
        this.totalPlayers = totalPlayers;
        this.name = name;
        this.player = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int getTotalPlayers() {
        return this.totalPlayers;
    }

    public int getNumberOfPlayersEntered() {
        return this.numberOfPlayersEntered;
    }

    public ArrayList<PlayerData> getPlayers() {
        return this.player;
    }

    public void addPlayer(PlayerData playerData) {
        this.player.add(playerData);
        this.numberOfPlayersEntered++;
    }

    public void removePlayer(PlayerData playerData) {
        this.player.remove(playerData);
        this.numberOfPlayersEntered--;
    }

    public void setName(String name) {
        this.name = name;
    }
}