package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event triggered when a player picks a cannon from the board.
 * This event is used to notify relevant components or systems about the action of a player selecting
 * a cannon, along with related details such as the chosen tile, associated connectors, and the cannon's strength.
 *
 * @param nickname       The unique player identifier for the player picking the cannon.
 * @param tileID         The ID of the tile from which the cannon was picked.
 * @param direction      The direction in which the cannon is oriented.
 * @param connectors     A list of connector type that are relevant to the cannon selection.
 * @param cannonStrength The strength value of the selected cannon.
 */
public record PickedCannonFromBoard(
    String nickname,
    int tileID,
    int direction,
    Integer[] connectors,
    float cannonStrength
) implements Event, Serializable {
}
