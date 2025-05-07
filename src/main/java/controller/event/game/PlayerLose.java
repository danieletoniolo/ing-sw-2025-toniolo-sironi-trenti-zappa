package controller.event.game;

import Model.Player.PlayerColor;
import controller.event.Event;

import java.io.Serializable;

public record PlayerLose(
        PlayerColor player
) implements Event, Serializable {
}
