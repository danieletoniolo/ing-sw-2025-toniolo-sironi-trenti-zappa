package it.polimi.ingsw.event.game.serverToClient.rotatedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is sent when a player rotates their shield.
 * @param nickname            is the userID of the player that has rotated the shield.
 * @param tileID              is the ID of the shield tile that has been rotated.
 * @param shieldingPositions  is an ArrayList of integers representing the positions of the shield after rotation (normally it shieldingPositions.size() == 2).
 * @param connectors          is a list of integers representing the connectors of the shield after rotation.
 */
public record RotatedShieldTile(
        String nickname,
        int tileID,
        int clockWise,
        ArrayList<Integer> shieldingPositions,
        int[] connectors
) implements Event, Serializable {
}
