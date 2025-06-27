package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player has used batteries.
 * It is used to notify the other players that the player has used batteries.
 * @param nickname     is the userID of the player who used the batteries.
 * @param batteriesIDs List of Pairs with the first element being the battery ID and the second element being the number of batteries in the battery.
 * @author Vittorio Sironi
 */
public record BatteriesLoss(
        String nickname,
        List<Pair<Integer, Integer>> batteriesIDs
) implements Event, Serializable {
}
