package controller.event.game;

import Model.SpaceShip.Cabin;
import Model.SpaceShip.SpaceShip;
import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public record AcceptAbandonedShip(UUID userID, int coins, int steps, Map<Integer, Cabin> cabins) implements Event, Serializable {
}
