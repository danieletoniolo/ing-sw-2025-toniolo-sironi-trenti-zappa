package controller.event.game;

import controller.event.Event;
import org.javatuples.Pair;

import java.io.Serializable;

public record CanProtect(
        Pair<Integer, Integer> canProtect
) implements Event, Serializable {
}
