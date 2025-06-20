package it.polimi.ingsw.event.game.serverToClient.spaceship;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to set the strength of the engine on the spaceship.
 * It is sent only to the client who has to set the strength of the engine in the view
 * @param singleEnginesStrength the strength of the single engines
 * @param maxEnginesStrength    the maximum strength of the engine
 */
public record SetEngineStrength(
        String nickname,
        int singleEnginesStrength,
        int maxEnginesStrength
) implements Event, Serializable {
}