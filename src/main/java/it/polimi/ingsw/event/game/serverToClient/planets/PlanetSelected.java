package it.polimi.ingsw.event.game.serverToClient.planets;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents an event where a player selects a planet in the game.
 * It notifies other players about the action taken.
 *
 * @param nickname     The username or identifier of the player selecting the planet.
 * @param planetNumber The unique identifier of the planet being selected.
 * @author Vittorio Sironi
 */
public record PlanetSelected(
        String nickname,
        int planetNumber
) implements Event, Serializable {
}
