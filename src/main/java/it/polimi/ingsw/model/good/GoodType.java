package it.polimi.ingsw.model.good;

import java.io.Serializable;

/**
 * Enumeration representing different types of goods.
 * Each good type has an associated integer value and implements Serializable.
 * @author Vittorio Sironi
 */
public enum GoodType implements Serializable {
    /** Blue colored good type with value 1. */
    BLUE(1),
    /** Green colored good type with value 2. */
    GREEN(2),
    /** Yellow colored good type with value 3. */
    YELLOW(3),
    /** Red colored good type with value 4. */
    RED(4);

    /** The integer value associated with this good type. */
    private final int value;

    /**
     * Constructor for GoodType enum.
     *
     * @param value the integer value to associate with this good type
     */
    GoodType(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this good type.
     *
     * @return the integer value of this good type
     */
    public int getValue() {
        return value;
    }

    /**
     * Retrieves a GoodType enum constant from its integer value.
     *
     * @param value the integer value to convert to a GoodType
     * @return the GoodType corresponding to the given value
     * @throws IllegalArgumentException if the value doesn't correspond to any GoodType
     */
    public static GoodType fromInt(int value) {
        for (GoodType goodType : GoodType.values()) {
            if (goodType.value == value) {
                return goodType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}