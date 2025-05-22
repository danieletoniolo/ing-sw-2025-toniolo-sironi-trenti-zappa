package event.game.serverToClient;

import event.eventType.Event;
import org.javatuples.Triplet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This event is used when a player have to add or remove a crew member.
 * @param nickname The nickname of the player that has to add or remove a crew member.
 * @param cabins A triplet containing in this order a cabin ID, the number of crew members lost and the type of crew member (0 normal, 1 brown alien, 2 purple alien)
 * */
public record UpdateCrewMembers(
        String nickname,
        List<Triplet<Integer, Integer, Integer>> cabins
) implements Event, Serializable {
}
