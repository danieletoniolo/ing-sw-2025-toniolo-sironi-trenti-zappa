package it.polimi.ingsw.view.miniModel.components.crewmembers;

import it.polimi.ingsw.view.miniModel.good.GoodView;

public enum CrewMembers {
    HUMAN(0),
    BROWALIEN(1),
    PURPLEALIEN(2);

    private final int value;
    private final String brown = "\033[38;5;220m";
    private final String purple = "\033[35m";
    private final String reset = "\033[0m";

    CrewMembers(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CrewMembers fromValue(int value) {
        for (CrewMembers crew : values()) {
            if (crew.value == value) {
                return crew;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Good component here
    }

    public String drawTui() {
        return switch (this) {
            case HUMAN -> "☺";
            case BROWALIEN -> brown + "&" + reset;
            case PURPLEALIEN -> purple + "&" + reset;
        };
    }

}
