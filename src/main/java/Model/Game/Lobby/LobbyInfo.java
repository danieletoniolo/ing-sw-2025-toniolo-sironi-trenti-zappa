package Model.Game.Lobby;

import Model.Game.Board.Level;

public class LobbyInfo {
    private String name;
    private final String founderNickname;
    private final Level level;
    private final int totalPlayers;
    private final int numberOfPlayersEntered;

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
        this.numberOfPlayersEntered = 0;
        this.totalPlayers = totalPlayers;
        this.level = level;
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