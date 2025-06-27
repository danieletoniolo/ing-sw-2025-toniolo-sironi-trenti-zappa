package it.polimi.ingsw.model.player;

import java.io.Serializable;

/**
 * Enumeration representing the available player colors in the game.
 * Each color has an associated integer value for identification purposes.
 * @author Vittorio Sironi
 */
public enum PlayerColor implements Serializable {
    /**
     * Blue player color with value 0
     */
    BLUE(0),
    /**
     * Green player color with value 1
     */
    GREEN(1),
    /**
     * Yellow player color with value 2
     */
    YELLOW(2),
    /**
     * Red player color with value 3
     */
    RED(3);

    /**
     * The integer value associated with this color
     */
    private final int value;

    /**
     * Constructor for PlayerColor enum
     * @param value the integer value to associate with this color
     */
    PlayerColor(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this color
     * @return the integer value of this color
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the first free color
     * @param colors the colors already taken
     * @return the first free color
     */
    public static PlayerColor getFreeColor(PlayerColor[] colors) {
        for (PlayerColor c : PlayerColor.values()) {
            boolean found = false;
            for (PlayerColor color1 : colors) {
                if (c == color1) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return c;
            }
        }
        return null;
    }

    /**
     * Converts an integer value to the corresponding PlayerColor enum
     * @param value the integer value to convert (0-3)
     * @return the PlayerColor corresponding to the given value
     * @throws IllegalArgumentException if the value is not valid (not between 0-3)
     */
    public static PlayerColor fromInt(int value) {
        for (PlayerColor color : PlayerColor.values()) {
            if (color.value == value) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
