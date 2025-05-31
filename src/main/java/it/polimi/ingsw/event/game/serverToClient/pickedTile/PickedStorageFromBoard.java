package it.polimi.ingsw.event.game.serverToClient.pickedTile;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event that is used to notify the system that a player has picked a specific storage unit
 * from the board. This event contains details about the player, the storage unit picked, and its configuration.
 *
 * @param nickname       The username or identifier of the player who picked the storage.
 * @param tileID         The identifier for the specific tile on the board where the storage was picked.
 * @param connectors     A list of IDs representing the connectors type associated with the picked storage.
 * @param dangerous      An integer indicating whether the storage is dangerous.
 * @param goodsCapacity  The capacity of goods that the picked storage can hold.
 */
public record PickedStorageFromBoard(
    String nickname,
    int tileID,
    Integer[] connectors,
    boolean dangerous,
    int goodsCapacity
) implements Event, Serializable {
}
