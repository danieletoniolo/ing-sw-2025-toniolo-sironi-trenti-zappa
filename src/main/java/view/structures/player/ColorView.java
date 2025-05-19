package view.structures.player;

public enum ColorView {
    RED, YELLOW, GREEN, BLUE;

    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";
    private final String player = "◉";

    public void drawGui() {
        //TODO: Implements Color Gui
    }

    public String drawTui() {
        return switch (this) {
            case RED -> red + player + reset;
            case YELLOW -> yellow + player + reset;
            case GREEN -> green + player + reset;
            case BLUE -> blue + player + reset;
        };
    }
}
