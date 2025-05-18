package view.structures.good;

public enum GoodView {
    BLUE(1), GREEN(2), YELLOW(3), RED(4);

    private final int value;
    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";
    private final String Cell = "■";

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
            case BLUE -> blue + Cell + reset;
            case GREEN -> green + Cell + reset;
            case YELLOW -> yellow + Cell + reset;
            case RED -> red + Cell + reset;
        };
    }
}
