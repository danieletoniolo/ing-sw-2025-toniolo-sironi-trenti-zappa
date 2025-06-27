package it.polimi.ingsw.view.miniModel.board;

/**
 * Enumeration representing different levels in the game.
 * Each level has an associated integer value.
 */
public enum LevelView {
    LEARNING(1), SECOND(2);

    private final int value;

    /**
     * Constructor for LevelView enum.
     *
     * @param value the integer value associated with this level
     */
    LevelView(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this level.
     *
     * @return the integer value of this level
     */
    public int getValue() {
        return value;
    }

    /**
     * Finds a LevelView enum constant by its integer value.
     *
     * @param value the integer value to search for
     * @return the LevelView enum constant with the specified value
     */
    public static LevelView fromValue(int value) {
        for (LevelView level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("No LevelView with value " + value);
    }
}
