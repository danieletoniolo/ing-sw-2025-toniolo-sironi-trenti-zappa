package controller.event.game;

import Model.SpaceShip.SpaceShip;
import controller.event.Event;

import java.io.Serializable;
import java.util.UUID;

public record SwapGoods(
        UUID userID,
        SpaceShip spaceShip
) implements Event, Serializable {
}
