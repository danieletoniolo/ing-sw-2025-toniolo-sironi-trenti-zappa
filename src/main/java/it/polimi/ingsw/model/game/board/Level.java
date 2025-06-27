package it.polimi.ingsw.model.game.board;

import java.io.Serializable;

/**
 * Enumeration representing different levels in the game.
 * Each level has an associated integer value.
 * @author Vittorio Sironi
 */
public enum Level implements Serializable {
    /** Learning level with value 1 */
    LEARNING(1),
    /** Second level with value 2 */
    SECOND(2);

    /** The integer value associated with this level */
    private final int value;

    /**
     * Constructor for Level enum.
     * @param value the integer value associated with this level
     */
    Level(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this level.
     * @return the integer value of this level
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts an integer value to the corresponding Level enum.
     * @param value the integer value to convert
     * @return the Level enum corresponding to the given value
     * @throws IllegalArgumentException if the value doesn't correspond to any Level
     */
    public static Level fromInt(int value) {
        for (Level level : Level.values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}