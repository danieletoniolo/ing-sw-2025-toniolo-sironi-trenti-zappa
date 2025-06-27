package it.polimi.ingsw.model.cards.hits;

import java.io.Serializable;

/**
 * Represents the four cardinal directions with associated integer values.
 * Used for defining movement or orientation in a 2D grid system.
 * @author Vittorio Sironi
 */
public enum Direction implements Serializable {
    /** North direction with value 0 */
    NORTH(0),
    /** West direction with value 1 */
    WEST(1),
    /** South direction with value 2 */
    SOUTH(2),
    /** East direction with value 3 */
    EAST(3);

    /** The integer value associated with this direction */
    private final int value;

    /**
     * Constructs a Direction with the specified integer value.
     * @param value the integer value to associate with this direction
     */
    Direction(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with this direction.
     * @return the integer value of this direction
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the Direction enum constant with the specified integer value.
     * @param value the integer value to convert to a Direction
     * @return the Direction corresponding to the given value
     * @throws IllegalArgumentException if the value does not correspond to any Direction
     */
    public static Direction fromInt(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
