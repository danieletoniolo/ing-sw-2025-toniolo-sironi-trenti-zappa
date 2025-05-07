package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record AddCoins(
        int coins
) implements Event, Serializable {
}
