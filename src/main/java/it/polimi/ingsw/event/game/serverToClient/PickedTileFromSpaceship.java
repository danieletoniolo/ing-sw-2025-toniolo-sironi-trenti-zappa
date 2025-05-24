package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This it.polimi.ingsw.event is used when a player pick a tile from the spaceship.
 * @param nickname is the nickname of the player that pick the tile.
 */
public record PickedTileFromSpaceship(
        String nickname
) implements Event, Serializable {}
