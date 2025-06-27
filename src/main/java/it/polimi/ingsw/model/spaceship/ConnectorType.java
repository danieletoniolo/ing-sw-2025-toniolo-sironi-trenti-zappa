package it.polimi.ingsw.model.spaceship;

/**
 * Represents the different types of connectors available for spaceship components.
 * Each connector type has an associated integer value for serialization purposes.
 * @author Vittorio Sironi
 */
public enum ConnectorType {
    /** Represents an empty connector with no connections */
    EMPTY(0),
    /** Represents a single connector allowing one connection */
    SINGLE(1),
    /** Represents a double connector allowing two connections */
    DOUBLE(2),
    /** Represents a triple connector allowing three connections */
    TRIPLE(3);

    /** The integer value associated with this connector type */
    private final int value;

    /**
     * Constructs a ConnectorType with the specified integer value.
     * @param value the integer value associated with this connector type
     */
    ConnectorType(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with this connector type.
     * @return the integer value of this connector type
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the ConnectorType corresponding to the given integer value.
     * @param value the integer value to convert
     * @return the ConnectorType with the specified value
     * @throws IllegalArgumentException if no ConnectorType matches the given value
     */
    public static ConnectorType fromInt(int value) {
        for (ConnectorType type : ConnectorType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid connector type value: " + value);
    }
}
