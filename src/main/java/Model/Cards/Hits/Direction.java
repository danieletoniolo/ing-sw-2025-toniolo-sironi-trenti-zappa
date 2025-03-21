package Model.Cards.Hits;

public enum Direction {
    NORTH(0), WEST(1), SOUTH(2), EAST(3);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
