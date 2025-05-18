package event.game;

import event.Event;
import org.javatuples.Pair;

import java.io.Serializable;

/**
 * This event is sent to the other client with the options that the player can use.
 * @param userID is the user username when the event is sent to the other client.
 *  *               The userID is the UUID when the event is sent from the client to the server.
 *  *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param canProtect Pair of the component that can shield and the value of the shield.
 *                   -1 if the ship can't shield, 0 if the ship can shield spending a battery, 1 if the ship can shield without spending a battery (in this case the component return is null)
 */
public record CanProtect(
        String userID,
        Pair<Integer, Integer> canProtect
) implements Event, Serializable {
}
