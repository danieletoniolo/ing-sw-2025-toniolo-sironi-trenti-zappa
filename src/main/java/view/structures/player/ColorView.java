package view.structures.player;

public enum ColorView {
    RED, YELLOW, GREEN, BLUE;

    public void drawGui() {
        //TODO: Implements Color Gui
    }

    public String drawTui() {
        return switch (this) {
            case RED -> "R";
            case YELLOW -> "Y";
            case GREEN -> "G";
            case BLUE -> "B";
        };
    }
}
