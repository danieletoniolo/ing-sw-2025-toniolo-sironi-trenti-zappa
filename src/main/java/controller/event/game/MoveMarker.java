package controller.event.game;

import Model.Player.PlayerColor;
import controller.event.Event;

import java.io.Serializable;

public record MoveMarker(
        PlayerColor player,
        int steps
) implements Event, Serializable {
}
