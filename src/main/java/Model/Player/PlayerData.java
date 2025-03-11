package Model.Player;

import Model.SpaceShip.SpaceShip;

public class PlayerData {
    private final String username;
    private final PlayerColor color;
    private int steps;

    private int coins;
    private final SpaceShip ship;
    private boolean leader;
    private boolean gaveUp;

    private boolean disconnected;

    /**
     * Create a new player
     * @param username the username of the player
     * @param color the color of the player
     * @param ship the ship of the player
     */
    public PlayerData(String username, PlayerColor color, SpaceShip ship) {
        this.username = username;
        this.color = color;
        this.ship = ship;
        this.steps = 0;
        this.coins = 0;
        this.leader = false;
        this.gaveUp = false;
        this.disconnected = false;
    }

    /**
     * Get the username of the player
     * @return the username of the player
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the color of the player
     * @return the color of the player
     */
    public PlayerColor getColor() {
        return this.color;
    }

    /**
     * Get the steps of the player
     * @return the steps of the player
     */
    public int getSteps() {
        return this.steps;
    }

    /**
     * Get the coins of the player
     * @return the coins of the player
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Get the laps of the player
     * @return the laps of the player
     */
    public int getNumberOfLaps(int numberOfCells) {
        return (int) (this.steps % numberOfCells);
    }

    /**
     * Get the leader of the game
     * @return the leader of the game
     */
    public boolean isLeader() {
        return this.leader;
    }

    /**
     * Get if the player is disconnected
     * @return the status of the connection of the player
     */
    public boolean isDisconnected() {
        return this.disconnected;
    }

    /**
     * Get if the player has given up
     * @return the ship of the player
     */
    public boolean hasGivenUp() {
        return this.gaveUp;
    }

    /**
     * Adds the specified number of coins to the player's coin count.
     * @param coins the number of coins to add
     */
    public void addCoins(int coins) {
        this.coins += coins;
    }

    /**
     * Adds the specified number of steps to the player's step count.
     * @param x the number of steps to add
     */
    public void addSteps(int x) {
        this.steps += x;
    }

    /**
     * Set the leader of the game
     * @param leader the leader of the game
     */
    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    /**
     * Set the gave up status of the player
     * @param gaveUp the gave up status of the player
     */
    public void setGaveUp(boolean gaveUp) {
        this.gaveUp = gaveUp;
    }

    /**
     * Set the disconnected status of the player
     * @param disconnected the disconnected status of the player
     */
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }
}
