package it.polimi.ingsw.model.cards.hits;

import java.io.Serializable;

public enum Direction implements Serializable {
    NORTH(0), WEST(1), SOUTH(2), EAST(3);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Direction fromInt(int value) {
        for (Direction direction : Direction.values()) {
            if (direction.value == value) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
