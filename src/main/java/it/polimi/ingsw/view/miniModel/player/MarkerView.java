package it.polimi.ingsw.view.miniModel.player;

import it.polimi.ingsw.model.player.PlayerColor;

public enum MarkerView {
    RED(3), YELLOW(2), GREEN(1), BLUE(0);

    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";
    private final String player = "◉";
    private int value;

    MarkerView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public static MarkerView fromValue(int value) {
        for (MarkerView color : MarkerView.values()) {
            if (color.value == value) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

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
