package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents the minimum player for the combat zone state.
 * @param nickname The nickname of the player that is the minimum player.
 */
public record MinPlayer(
        String nickname
) implements Event, Serializable {
}
