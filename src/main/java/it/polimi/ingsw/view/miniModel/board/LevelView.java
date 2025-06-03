package it.polimi.ingsw.view.miniModel.board;

public enum LevelView {
    LEARNING(1), SECOND(2);

    private final int value;

    LevelView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LevelView fromValue(int value) {
        for (LevelView level : values()) {
            if (level.value == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("No LevelView with value " + value);
    }
}
