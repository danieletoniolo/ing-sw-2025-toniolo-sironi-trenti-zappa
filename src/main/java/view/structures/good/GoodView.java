package view.structures.good;

public enum GoodView {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    private final int value;

    GoodView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Good component here
    }

    public String drawTui() {
        return switch (this) {
            case BLUE -> "B";
            case GREEN -> "G";
            case YELLOW -> "Y";
            case RED -> "R";
        };
    }
}
