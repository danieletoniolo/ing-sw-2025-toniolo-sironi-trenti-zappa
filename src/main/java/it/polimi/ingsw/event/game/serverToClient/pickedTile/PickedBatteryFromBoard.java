package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This record represents an event where a player picks up a battery from the board.
 * It is used to notify the system or other components about this action and
 * provides details about the battery and the player involved.
 *
 * @param nickname          The username or identifier of the player who picked up the battery.
 * @param tileID            The identifier of the tile from which the battery was picked.
 * @param clockwiseRotation The rotation of the battery in a clockwise direction, represented as an integer.
 * @param connectors        A list of the connector type associated with the battery.
 * @param energyNumber      The amount of energy represented by the picked battery.
 */
public record PickedBatteryFromBoard(
        String nickname,
        int tileID,
        int clockwiseRotation,
        int[] connectors,
        int energyNumber
) implements Event, Serializable {
}
