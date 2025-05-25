package it.polimi.ingsw.model.spaceship;

public enum ConnectorType {
    EMPTY(0), SINGLE(1), DOUBLE(2), TRIPLE(3);

    private final int value;
    ConnectorType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ConnectorType fromInt(int value) {
        for (ConnectorType type : ConnectorType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid connector type value: " + value);
    }
}
