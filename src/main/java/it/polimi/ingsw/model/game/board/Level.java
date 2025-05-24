package it.polimi.ingsw.model.game.board;

import java.io.Serializable;

public enum Level implements Serializable {
    LEARNING(1), SECOND(2);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}