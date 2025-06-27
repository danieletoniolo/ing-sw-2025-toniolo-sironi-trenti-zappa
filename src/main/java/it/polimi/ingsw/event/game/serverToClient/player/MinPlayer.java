package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This record represents the minimum player for the combat zone state.
 * @param nickname The userID of the player that is the minimum player.
 * @author Vittorio Sironi
 */
public record MinPlayer(
        String nickname
) implements Event, Serializable {
}
