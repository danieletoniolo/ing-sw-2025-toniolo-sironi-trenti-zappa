package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player has used batteries.
 * It is used to notify the other players that the player has used batteries.
 * @param nickname     is the userID of the player who used the batteries.
 * @param batteriesIDs The IDs of the batteries used.
 */
public record BatteriesUsed(
        String nickname,
        List<Integer> batteriesIDs
) implements Event, Serializable {
}
