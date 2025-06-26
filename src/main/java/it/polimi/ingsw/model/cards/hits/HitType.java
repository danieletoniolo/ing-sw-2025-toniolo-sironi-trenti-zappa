package it.polimi.ingsw.model.cards.hits;

import java.io.Serializable;

/**
 * Enumeration representing different types of hits that can occur in the game.
 * Each hit type has an associated integer value for serialization and identification purposes.
 * @author Vittorio Sironi
 */
public enum HitType implements Serializable {
    /** Small meteor hit with value 0 */
    SMALLMETEOR(0),
    /** Large meteor hit with value 1 */
    LARGEMETEOR(1),
    /** Light fire hit with value 2 */
    LIGHTFIRE(2),
    /** Heavy fire hit with value 3 */
    HEAVYFIRE(3);

    /** The integer value associated with this hit type */
    private final int value;

    /**
     * Constructor for HitType enum.
     * @param value the integer value to associate with this hit type
     */
    HitType(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this hit type.
     * @return the integer value of this hit type
     */
    public int getValue() {
        return value;
    }

    /**
     * Creates a HitType from an integer value.
     * @param value the integer value to convert
     * @return the HitType corresponding to the given value
     * @throws IllegalArgumentException if the value doesn't correspond to any HitType
     */
    public static HitType fromInt(int value) {
        for (HitType hitType : HitType.values()) {
            if (hitType.value == value) {
                return hitType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
