package controller.event.game;

import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public record UseEngine(
        UUID userID,
        float enginesPowerToUse,
        ArrayList<Integer> batteriesIDs
) implements Event, Serializable {
}
