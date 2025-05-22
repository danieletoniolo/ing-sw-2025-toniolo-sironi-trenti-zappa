package Model.Player;

import Model.SpaceShip.SpaceShip;

import java.io.Serializable;
import java.util.UUID;

public class PlayerData implements Serializable {
    private final String username;
    private final UUID uuid;
    private final PlayerColor color;
    private int step;
    private int position;

    private int coins;
    private final SpaceShip ship;
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
        this.uuid = UUID.fromString(username);
        this.color = color;
        this.ship = ship;
        this.coins = 0;
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
     * Get the UUID of the player
     * @return the UUID of the player
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Get the color of the player
     * @return the color of the player
     */
    public PlayerColor getColor() {
        return this.color;
    }

    /**
     * Set the position of the player
     * @param step step of the player
     */
    public void setStep(int step) {
        this.step = step;
    }

    /**
     * Get the position of the player
     * @return the position of the player
     */
    public int getStep() {
        return this.step;
    }

    /**
     * Set position: 0 = leader, 1 = second, ...
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Get the position of the player: 0 = leader, 1 = second, ...
     * @return position of the player
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Get the coins of the player
     * @return the coins of the player
     */
    public int getCoins() {
        return this.coins;
    }

    /**
     * Get the ship of the player
     * @return the ship of the player
     */
    public SpaceShip getSpaceShip() {
        return this.ship;
    }

    /**
     * Get the leader of the game
     * @return the leader of the game
     */
    public boolean isLeader() {
        return position == 0;
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
     * @return true if the player has given up, false otherwise
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

    /**
     * Check if the player is equal to another player
     * @param p the player to compare
     * @return true if the players are equal, false otherwise
     */
    @Override
    public boolean equals(Object p) {
        if (p == null) {
            return false;
        }
        if (p instanceof PlayerData) {
            PlayerData player = (PlayerData) p;
            return this.uuid.equals(player.getUUID());
        }
        return false;
    }
}
