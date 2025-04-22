package controller.event.game;

import Model.SpaceShip.SpaceShip;
import controller.event.Event;

import java.io.Serializable;

public record RemoveCrew(
        SpaceShip spaceShip
) implements Event, Serializable {
}
