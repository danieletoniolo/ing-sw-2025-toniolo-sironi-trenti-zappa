package it.polimi.ingsw.view.miniModel.player;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.spaceship.SpaceShipView;

/**
 * Represents the data view of a player in the mini model.
 * Stores player information such as username, color, marker, coins, ship, and stats.
 * Implements the Structure interface for TUI drawing.
 */
public class PlayerDataView implements Structure {
    /** The username of the player. */
    private final String username;
    /** The ANSI color code associated with the player. */
    private final String color;
    /** The marker view representing the player's color. */
    private final MarkerView markerView;
    /** The number of coins the player has. */
    private int coins;
    /** The current strength of the player's cannons. */
    private float cannonsStrength;
    /** The maximum potential strength of the player's cannons. */
    private float maxPotentialCannonsStrength;
    /** The current strength of the player's engines. */
    private int enginesStrength;
    /** The maximum potential strength of the player's engines. */
    private int maxPotentialEnginesStrength;
    /** The player's spaceship view. */
    private final SpaceShipView ship;
    /** The component currently in the player's hand. */
    private ComponentView hand;
    /** ANSI escape code for blue color. */
    private final String blue =   "\033[34m";
    /** ANSI escape code for green color. */
    private final String green =  "\033[32m";
    /** ANSI escape code for yellow color. */
    private final String yellow = "\033[33m";
    /** ANSI escape code for red color. */
    private final String red =    "\033[31m";
    /** ANSI escape code to reset color. */
    private final String reset =  "\033[0m";

    /**
     * Constructs a PlayerDataView with the specified username, marker color, and ship.
     *
     * @param username the player's username
     * @param color the marker view representing the player's color
     * @param ship the player's spaceship view
     */
    public PlayerDataView(String username, MarkerView color, SpaceShipView ship) {
        this.username = username;
        this.markerView = color;
        this.color = switch (color) {
            case BLUE -> blue;
            case GREEN -> green;
            case YELLOW -> yellow;
            case RED -> red;
        };
        this.ship = ship;
    }

    /**
     * Returns the number of rows to draw for the player data in the TUI.
     *
     * @return the number of rows to draw
     */
    public int getRowsToDraw() {
        return 4;
    }

    /**
     * Draws a specific line of the player's data for the TUI.
     *
     * @param line the line number to draw
     * @return the string representation of the specified line
     * @throws IllegalStateException if the line number is not valid
     */
    @Override
    public String drawLineTui(int line) {
        return switch (line) {
            case 0 -> color + username + reset;
            case 1 -> "Coins: " + coins;
            case 2 -> "Cannons strength: " + cannonsStrength + " (max potential: " + maxPotentialCannonsStrength + ")";
            case 3 -> "Engines strength: " + enginesStrength + " (max potential: " + maxPotentialEnginesStrength + ")";
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    /**
     * Returns the username of the player.
     *
     * @return the player's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the number of coins the player has.
     *
     * @param coins the number of coins
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }

    /**
     * Returns the number of coins the player has.
     *
     * @return the number of coins
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Returns the player's spaceship view.
     *
     * @return the player's spaceship view
     */
    public SpaceShipView getShip() {
        return ship;
    }

    /**
     * Sets the component currently in the player's hand.
     *
     * @param hand the component view to set as hand
     */
    public void setHand(ComponentView hand) {
        this.hand = hand;
    }

    /**
     * Returns the component currently in the player's hand.
     *
     * @return the component in hand
     */
    public ComponentView getHand() {
        return hand;
    }

    /**
     * Returns the marker view representing the player's color.
     *
     * @return the marker view
     */
    public MarkerView getMarkerView() {
        return markerView;
    }

    /**
     * Sets the current strength of the player's cannons.
     *
     * @param cannonsStrength the current cannons strength
     */
    public void setCannonsStrength(float cannonsStrength) {
        this.cannonsStrength = cannonsStrength;
    }

    /**
     * Sets the maximum potential strength of the player's cannons.
     *
     * @param maxPotentialCannonsStrength the maximum potential cannons strength
     */
    public void setMaxPotentialCannonsStrength(float maxPotentialCannonsStrength) {
        this.maxPotentialCannonsStrength = maxPotentialCannonsStrength;
    }

    /**
     * Sets the current strength of the player's engines.
     *
     * @param enginesStrength the current engines strength
     */
    public void setEnginesStrength(int enginesStrength) {
        this.enginesStrength = enginesStrength;
    }

    /**
     * Sets the maximum potential strength of the player's engines.
     *
     * @param maxPotentialEnginesStrength the maximum potential engines strength
     */
    public void setMaxPotentialEnginesStrength(int maxPotentialEnginesStrength) {
        this.maxPotentialEnginesStrength = maxPotentialEnginesStrength;
    }
}
