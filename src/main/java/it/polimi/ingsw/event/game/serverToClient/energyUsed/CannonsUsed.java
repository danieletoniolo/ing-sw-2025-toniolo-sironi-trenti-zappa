package it.polimi.ingsw.event.game.serverToClient.energyUsed;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player has used the cannons.
 * It is used to notify the other players that the player has used the cannons.
 * @param nickname     is the user userID. Only the user know his ID, so the event is not faked.
 * @param cannonsIDs   The IDs of the cannons to use.
 * @param batteriesIDs List of Pairs with the first element being the battery ID and the second element being the number of batteries in the battery.v
 * */
public record CannonsUsed(
        String nickname,
        List<Integer> cannonsIDs,
        List<Pair<Integer, Integer>> batteriesIDs
) implements Event, Serializable {}
