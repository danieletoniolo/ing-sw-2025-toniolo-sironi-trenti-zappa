package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an event that notifies the rotation of a cannon tile.
 * This record encapsulates the nickname of the player who rotated the cannon,
 * the ID of the tile, the direction of rotation, and the connectors associated with it.
 *
 * @param nickname    The nickname of the player who rotated the cannon.
 * @param tileID      The identifier of the cannon tile that was rotated.
 * @param direction   The direction in which the cannon was rotated.
 * @param connectors  A list of integers representing the connectors associated with the cannon.
 */
public record RotatedCannonTile(
        String nickname,
        int tileID,
        int direction,
        List<Integer> connectors
) implements Event, Serializable {
}
