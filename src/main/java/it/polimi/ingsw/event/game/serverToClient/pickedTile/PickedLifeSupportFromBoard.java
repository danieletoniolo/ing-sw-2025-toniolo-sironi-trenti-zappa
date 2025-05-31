package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event where a player picks up life support from a specific tile on the board.
 * This event is used to notify the system or other players about the life support
 * being picked up and its corresponding details.
 *
 * @param nickname   The username or identifier of the player picking the life support.
 * @param tileID     The ID of the tile from which the life support was picked.
 * @param connectors A list of integers representing the connectors type of the tile, which may
 *                   determine potential connections or associations within the board.
 * @param type       The type of the life support picked up:
 *                   1 represents a "brown alien", 2 represents a "purple alien".
 */
public record PickedLifeSupportFromBoard(
        String nickname,
        int tileID,
        Integer[] connectors,
        int type
) implements Event, Serializable {
}
