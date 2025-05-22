package event.game.serverToClient;

import event.eventType.Event;
import org.javatuples.Pair;

import java.io.Serializable;

/**
 * This event is sent to the other client with the options that the player can use.
 * @param nickname   The nickname of the player that can use the options.
 * @param canProtect Pair of the component that can shield and the value of the shield.
 *                   -1 if the ship can't shield, 0 if the ship can shield spending a battery, 1 if the ship can shield without spending a battery (in this case the component return is null)
 */
public record CanProtect(
        String nickname,
        Pair<Integer, Integer> canProtect
) implements Event, Serializable {
}
