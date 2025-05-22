package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player place a tile on the spaceship.
 * @param nickname is the nickname of the player that placed the tile.
 * @param row      is the row of the tile on the spaceship.
 * @param column   is the column of the tile on the spaceship.
 */
public record PlacedTileToSpaceship(
        String nickname,
        int row,
        int column
) implements Event, Serializable {}
