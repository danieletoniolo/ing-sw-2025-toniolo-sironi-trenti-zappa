package it.polimi.ingsw.view.miniModel;

public enum GamePhases {
    LOBBY(0),
    BUILDING(1),
    VALIDATION(2),
    CREW(3),
    CARDS(4),
    FINISHED(5);

    private final int value;
    GamePhases(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GamePhases fromValue(int value) {
        for (GamePhases phase : GamePhases.values()) {
            if (phase.value == value) {
                return phase;
            }
        }
        throw new IllegalArgumentException("Invalid value for GameState: " + value);
    }
}
