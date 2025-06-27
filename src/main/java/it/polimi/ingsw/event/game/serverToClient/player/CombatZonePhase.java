package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents a phase in the combat zone of the game.
 * @param phaseNumber the number of the phase
 *                    0, 1, 2
 * @author Vittorio Sironi
 */
public record CombatZonePhase(
        int phaseNumber
) implements Event, Serializable {
}
