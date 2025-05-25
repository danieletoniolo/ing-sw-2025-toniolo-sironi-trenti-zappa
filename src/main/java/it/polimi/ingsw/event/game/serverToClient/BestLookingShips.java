package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the best looking ships in the game.
 * @param nicknames The list of users' nicknames with the best looking ships
 */
public record BestLookingShips(
        List<String> nicknames
) implements Event, Serializable {
}
