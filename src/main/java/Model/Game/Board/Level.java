package Model.Game.Board;

public enum Level {
    LEARNING(1), SECOND(2);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}