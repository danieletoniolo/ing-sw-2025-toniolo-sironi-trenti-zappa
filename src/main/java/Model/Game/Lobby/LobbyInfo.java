package Model.Game.Lobby;

import Model.Player.PlayerData;

import java.util.ArrayList;

public class LobbyInfo {
    private final ArrayList<PlayerData> players;
    private String name;
    private final int totalPlayers;
    private int numberOfPlayersEntered;

    /**
     * Create a new lobby
     * @param name the name of the lobby
     * @param totalPlayers the total number of players in the lobby
     */
    public LobbyInfo(String name, int totalPlayers) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Lobby name cannot be null or empty");
        }
        if (totalPlayers <= 0 || totalPlayers > 4) {
            throw new IndexOutOfBoundsException("Total players must be between 1 and 4");
        }
        this.numberOfPlayersEntered = 0;
        this.totalPlayers = totalPlayers;
        this.name = name;
        this.players = new ArrayList<>();
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
        return this.players;
    }

    /**
     * Add a player to the lobby
     * @param player the player to add
     */
    public void addPlayer(PlayerData player) throws NullPointerException, IllegalStateException {
        if (player == null) {
            throw new NullPointerException("Player cannot be null");
        }
        if (this.numberOfPlayersEntered >= this.totalPlayers) {
            throw new IllegalStateException("Lobby is full");
        }
        if (players.contains(player)) {
            throw new IllegalStateException("Player already in lobby");
        }
        this.players.add(player);
        this.numberOfPlayersEntered++;
    }

    /**
     * Remove a player from the lobby
     * @param player the player to remove
     */
    public void removePlayer(PlayerData player) throws NullPointerException, IllegalStateException{
        if (player == null) {
            throw new NullPointerException("Player cannot be null");
        }
        if (!this.players.contains(player)) {
            throw new IllegalStateException("Player not in lobby");
        }
        this.players.remove(player);
        this.numberOfPlayersEntered--;
    }

    /**
     * Set the name of the lobby
     * @param name the name of the lobby
     */
    public void setName(String name) {
        this.name = name;
    }

    public boolean canGameStart() {
        return this.numberOfPlayersEntered == this.totalPlayers;
    }
}