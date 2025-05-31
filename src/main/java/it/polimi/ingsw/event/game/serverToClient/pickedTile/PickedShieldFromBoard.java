package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is triggered when a player picks a shield from the board.
 * It notifies the system or other components about the shield selection action.
 *
 * @param nickname           is the userID of the player who performed the action.
 * @param tileID             is the identifier of the tile where the shield was located on the board.
 * @param shieldingPositions is an ArrayList of integers representing the positions of the shield
 * @param connectors         is a list of integers representing the connectors type associated
 *                           with the shield on the given tile.
 */
public record PickedShieldFromBoard(
    String nickname,
    int tileID,
    ArrayList<Integer> shieldingPositions,
    Integer[] connectors
) implements Event, Serializable {
}
