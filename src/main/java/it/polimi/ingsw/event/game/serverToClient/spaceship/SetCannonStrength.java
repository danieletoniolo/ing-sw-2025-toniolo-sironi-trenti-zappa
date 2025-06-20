package it.polimi.ingsw.event.game.serverToClient.spaceship;


import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to set the strength of the cannons on the spaceship.
 * @param singleCannonsStrength the strength of the single cannons
 * @param maxCannonsStrength    the maximum strength of the cannons
 */
public record SetCannonStrength(
        String nickname,
        float singleCannonsStrength,
        float maxCannonsStrength
) implements Event, Serializable {
}
