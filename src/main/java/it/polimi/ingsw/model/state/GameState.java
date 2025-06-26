package it.polimi.ingsw.model.state;

/**
 * Enumeration representing the different states of a game.
 * Each state has an associated integer value for serialization purposes.
 * @author Vittorio Sironi
 */
public enum GameState {
    /** Initial state where players join the game */
    LOBBY(0),
    /** State where players build their structures */
    BUILDING(1),
    /** State where game validates player actions */
    VALIDATION(2),
    /** State where players manage their crew */
    CREW(3),
    /** State where players interact with cards */
    CARDS(4),
    /** State where players receive rewards */
    REWARD(5),
    /** Final state when the game has ended */
    FINISHED(6);

    /** The integer value associated with this game state for serialization purposes */
    private final int value;

    /**
     * Constructor for GameState enum.
     * @param value the integer value associated with this state
     */
    GameState(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this game state.
     * @return the integer value of this state
     */
    public int getValue() {
        return value;
    }

    /**
     * Converts an integer value to the corresponding GameState.
     * @param value the integer value to convert
     * @return the GameState corresponding to the given value
     * @throws IllegalArgumentException if the value doesn't correspond to any GameState
     */
    public static GameState fromInt(int value) {
        for (GameState state : GameState.values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameState: " + value);
    }
}
