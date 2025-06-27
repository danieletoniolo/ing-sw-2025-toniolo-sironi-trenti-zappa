package it.polimi.ingsw.model.state;

/**
 * Enumeration representing different types of penalties that can be applied.
 * Each penalty type has an associated integer value.
 */
public enum PenaltyType {
    /** Penalty related to crew */
    CREW_PENALTY(0),
    /** Penalty related to goods */
    GOODS_PENALTY(1),
    /** Penalty related to batteries */
    BATTERIES_PENALTY(2),
    /** Penalty applied when hit */
    HIT_PENALTY(3);

    /** The integer value associated with this penalty type */
    private final int value;

    /**
     * Constructor for PenaltyType.
     *
     * @param value the integer value associated with this penalty type
     */
    PenaltyType(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this penalty type.
     *
     * @return the integer value of this penalty type
     */
    public int getValue() {
        return value;
    }
}
