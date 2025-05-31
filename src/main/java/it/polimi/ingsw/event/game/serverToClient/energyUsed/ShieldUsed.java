package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player uses a shield.
 * @param nickname  is the userID of the player who used the shield.
 * @param batteryID is the ID of the battery used to use the shield.
 */
public record ShieldUsed(
        String nickname,
        int batteryID
) implements Event, Serializable {}
