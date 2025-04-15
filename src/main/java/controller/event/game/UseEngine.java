package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record UseEngine(
    int energyUsed
) implements Event, Serializable {
}
