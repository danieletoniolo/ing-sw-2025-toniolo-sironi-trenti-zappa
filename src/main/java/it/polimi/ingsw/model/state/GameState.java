package it.polimi.ingsw.model.state;

public enum GameState {
    LOBBY(0),
    BUILDING(1),
    VALIDATION(2),
    CREW(3),
    CARDS(4),
    REWARD(5),
    FINISHED(6);

    private final int value;
    GameState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameState fromInt(int value) {
        for (GameState state : GameState.values()) {
            if (state.value == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameState: " + value);
    }
}
