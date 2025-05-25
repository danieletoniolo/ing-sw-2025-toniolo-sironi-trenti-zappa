package it.polimi.ingsw.model.good;

import java.io.Serializable;

public enum GoodType implements Serializable {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    private final int value;

    GoodType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GoodType fromInt(int value) {
        for (GoodType goodType : GoodType.values()) {
            if (goodType.value == value) {
                return goodType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}