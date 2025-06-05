package it.polimi.ingsw.model.player;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    BLUE(0), GREEN(1), YELLOW(2), RED(3);

    private final int value;

    PlayerColor(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Get the first free color
     * @param colors the colors already taken
     * @return the first free color
     */
    public static PlayerColor getFreeColor(PlayerColor[] colors) {
        for (PlayerColor c : PlayerColor.values()) {
            boolean found = false;
            for (PlayerColor color1 : colors) {
                if (c == color1) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return c;
            }
        }
        return null;
    }

    public static PlayerColor fromInt(int value) {
        for (PlayerColor color : PlayerColor.values()) {
            if (color.value == value) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
