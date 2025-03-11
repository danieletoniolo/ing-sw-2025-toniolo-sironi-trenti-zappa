package Model.Game.Lobby;

import Model.Player.PlayerData;

import java.util.ArrayList;

public class LobbyInfo {
    private final ArrayList<PlayerData> player;
    private String name;
    private final int totalPlayers;
    private int numberOfPlayersEntered;

    /**
     * Create a new lobby
     * @param name the name of the lobby
     * @param totalPlayers the total number of players in the lobby
     */
    public LobbyInfo(String name, int totalPlayers) {
        this.numberOfPlayersEntered = 0;
        this.totalPlayers = totalPlayers;
        this.name = name;
        this.player = new ArrayList<>();
    }

    /**
     * Get the name of the lobby
     * @return the name of the lobby
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the total number of players in the lobby
     * @return the total number of players in the lobby
     */
    public int getTotalPlayers() {
        return this.totalPlayers;
    }

    /**
     * Get the number of players entered in the lobby
     * @return the number of players entered in the lobby
     */
    public int getNumberOfPlayersEntered() {
        return this.numberOfPlayersEntered;
    }

    /**
     * Get the players in the lobby
     * @return the players in the lobby
     */
    public ArrayList<PlayerData> getPlayers() {
        return this.player;
    }

    /**
     * Add a player to the lobby
     * @param playerData the player to add
     */
    public void addPlayer(PlayerData playerData) {
        this.player.add(playerData);
        this.numberOfPlayersEntered++;
    }

    /**
     * Remove a player from the lobby
     * @param playerData the player to remove
     */
    public void removePlayer(PlayerData playerData) {
        this.player.remove(playerData);
        this.numberOfPlayersEntered--;
    }

    /**
     * Set the name of the lobby
     * @param name the name of the lobby
     */
    public void setName(String name) {
        this.name = name;
    }
}