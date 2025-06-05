package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;

/**
 * This event is used when a player uses a shield.
 * @param nickname  is the userID of the player who used the shield.
 * @param batteryID Pair with the first element being the battery ID and the second element being the number of batteries in the battery.
 */
public record ShieldUsed(
        String nickname,
        Pair<Integer, Integer> batteryID
) implements Event, Serializable {}
