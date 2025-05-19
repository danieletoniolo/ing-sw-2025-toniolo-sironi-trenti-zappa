package Model.Player;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    BLUE, GREEN, YELLOW, RED;

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
}
