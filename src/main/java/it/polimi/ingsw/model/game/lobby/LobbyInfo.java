package it.polimi.ingsw.model.game.lobby;

import it.polimi.ingsw.model.game.board.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyInfo implements Serializable {
    private String name;
    private final String founderNickname;
    private final Level level;
    private final int totalPlayers;
    private  int numberOfPlayersEntered;
    private final List<UUID> playersReady;

    /**
     * Create a new lobby
     *
     * @param founderNickname the name of the lobby's founder
     * @param totalPlayers    the total number of players in the lobby
     * @throws IllegalArgumentException  if the name is null or empty
     * @throws IndexOutOfBoundsException if the total players is less than 1 or greater than 4
     */
    public LobbyInfo(String founderNickname, int totalPlayers, Level level) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (founderNickname == null || founderNickname.isEmpty()) {
            throw new IllegalArgumentException("Founder's nickname cannot be null or empty");
        }
        if (totalPlayers <= 0 || totalPlayers > 4) {
            throw new IndexOutOfBoundsException("Total players must be between 1 and 4");
        }
        this.name = founderNickname + "'s lobby";
        this.founderNickname = founderNickname;
        this.totalPlayers = totalPlayers;
        this.level = level;
        this.playersReady = new ArrayList<>();
        this.numberOfPlayersEntered = 0;
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
     * Get the number of players in the lobby
     * @return the number of players in the lobby
     */
    public int getNumberOfPlayersEntered() {
        return this.numberOfPlayersEntered;
    }

    /**
     * Set the number of players in the lobby
     * @return the number of players in the lobby
     */
    public String getFounderNickname() {
        return this.founderNickname;
    }

    /**
     * Get the level of the lobby
     * @return the level of the lobby
     */
    public Level getLevel() {
        return this.level;
    }

    /**
     * Get if a player is ready in the lobby
     * @return a boolean indicating whether the player is ready
     */
    public boolean isPlayerReady(UUID uuid) {
        return this.playersReady.contains(uuid);
    }

    /**
     * Set the name of the lobby
     * @param name the name of the lobby
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Adds a player to the list of players who are marked as ready in the lobby.
     * If the player is already in the list, no action is performed.
     * @param playerId the unique identifier of the player to be marked as ready
     */
    public void addPlayerReady(UUID playerId) {
        if (this.playersReady.size() < this.totalPlayers) {
            if (!this.playersReady.contains(playerId)) {
                this.playersReady.add(playerId);
            }
        } else {
            throw new IllegalStateException("Game has already started, cannot add player to ready list");
        }
    }

    /**
     * Increments the number of players who have entered the lobby.
     * If the number of players exceeds the total allowed, an exception is thrown.
     */
    public void addPlayer() {
        if (this.numberOfPlayersEntered < this.totalPlayers) {
            this.numberOfPlayersEntered++;
        } else {
            throw new IllegalStateException("Lobby is full, cannot add more players");
        }
    }

    /**
     * Removes a player from the lobby by decrementing the number of players entered.
     * If there are no players to remove, an exception is thrown.
     */
    public void removePlayer() {
        if (this.numberOfPlayersEntered > 0) {
            this.numberOfPlayersEntered--;
        } else {
            throw new IllegalStateException("No players to remove from the lobby");
        }
    }

    /**
     * Removes the specified player from the list of players marked as ready in the lobby.
     * @param playerId the unique identifier of the player to be removed from the ready list
     */
    public void removePlayerReady(UUID playerId) {
        if (!this.playersReady.isEmpty()) {
            this.playersReady.remove(playerId);
        } else {
            throw new IllegalStateException("Game has already started, cannot remove player from ready list");
        }
    }

    /**
     * Checks if the game can start based on the number of players marked as ready.
     * @return true if the number of players ready matches the total players, false otherwise
     */
    public boolean canGameStart() {
        return this.playersReady.size() == this.totalPlayers;
    }
}