package controller.event.game;

import Model.Player.PlayerColor;
import controller.event.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public record CrewLoss(
        PlayerColor player,
        ArrayList<Pair<Integer, Integer>> cabinsIDs
) implements Event, Serializable {
}
