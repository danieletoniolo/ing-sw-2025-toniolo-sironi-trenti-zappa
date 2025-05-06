package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record UseEnergy(
        int batteryID
) implements Event, Serializable {
}
