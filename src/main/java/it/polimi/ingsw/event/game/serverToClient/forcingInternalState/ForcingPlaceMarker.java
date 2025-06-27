package it.polimi.ingsw.event.game.serverToClient.forcingInternalState;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents the forcing place marker event.
 * @param nickname The nickname of the player that is forced to place a marker.
 * @author Vittorio Sironi
 */
public record ForcingPlaceMarker(
    String nickname
) implements Event, Serializable {
}
