package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param nickname     The userID of the user. Only the user know his userID, so the event is not faked.
 * @param enginesIDs   The IDs of the engines to use.
 * @param batteriesIDs List of Pairs with the first element being the battery ID and the second element being the number of batteries in the battery.
 */
public record EnginesUsed(
        String nickname,
        List<Integer> enginesIDs,
        List<Pair<Integer, Integer>> batteriesIDs
) implements Event, Serializable {}
