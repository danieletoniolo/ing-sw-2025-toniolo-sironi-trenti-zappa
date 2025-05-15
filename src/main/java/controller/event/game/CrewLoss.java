package controller.event.game;

import Model.Player.PlayerColor;
import controller.event.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player have lost a crew member.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param cabinsIDs The list of cabins IDs where the crew members are lost
 *                  The pair represents the cabin ID and the number of crew members lost
 * */
public record CrewLoss(
        String userID,
        ArrayList<Pair<Integer, Integer>> cabinsIDs
) implements Event, Serializable {
}
