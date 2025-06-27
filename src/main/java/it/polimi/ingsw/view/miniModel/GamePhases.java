package it.polimi.ingsw.view.miniModel;

/**
 * Enum representing the different phases of the game.
 * Each phase is associated with an integer value.
 */
public enum GamePhases {
    LOBBY(0),
    BUILDING(1),
    VALIDATION(2),
    CREW(3),
    CARDS(4),
    REWARD(5),
    FINISHED(6);

    private final int value;

    /**
     * Constructs a GamePhases enum with the specified integer value.
     *
     * @param value the integer value associated with the phase
     */
    GamePhases(int value) {
        this.value = value;
    }

    /**
     * Returns the integer value associated with this game phase.
     *
     * @return the integer value of the phase
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the GamePhases enum corresponding to the given integer value.
     *
     * @param value the integer value of the phase
     * @return the corresponding GamePhases enum
     * @throws IllegalArgumentException if the value does not correspond to any phase
     */
    public static GamePhases fromValue(int value) {
        for (GamePhases phase : GamePhases.values()) {
            if (phase.value == value) {
                return phase;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameState: " + value);
    }
}
