package Model.Game.Lobby;

import java.util.UUID;

public class LobbyInfo {
    private String name;
    private final int totalPlayers;
    private int numberOfPlayersEntered;

    /**
     * Create a new lobby
     *
     * @param founderNickname the name of the lobby's founder
     * @param totalPlayers    the total number of players in the lobby
     * @throws IllegalArgumentException  if the name is null or empty
     * @throws IndexOutOfBoundsException if the total players is less than 1 or greater than 4
     */
    public LobbyInfo(String founderNickname, int totalPlayers) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (founderNickname == null || founderNickname.isEmpty()) {
            throw new IllegalArgumentException("Founder's nickname cannot be null or empty");
        }
        if (totalPlayers <= 0 || totalPlayers > 4) {
            throw new IndexOutOfBoundsException("Total players must be between 1 and 4");
        }
        this.name = founderNickname + "'s lobby";
        this.numberOfPlayersEntered = 0;
        this.totalPlayers = totalPlayers;
    }

    /**
     * Get the name of the lobby
     * @return the name of the lobby
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the UUID of the lobby
     * @return the UUID of the lobby
     */
    public UUID getUUID() {
        return this.uuid;
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