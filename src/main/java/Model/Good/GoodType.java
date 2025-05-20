package Model.Good;

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
}