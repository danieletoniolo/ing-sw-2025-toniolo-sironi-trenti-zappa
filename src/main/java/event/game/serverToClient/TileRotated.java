package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is sent when the user rotates a tile.
 * @param nickname is the nickname of the player that has rotated the tile.
 * @param tileID   is the ID of the tile that has been rotated.
 */
public record TileRotated(
        String nickname,
        int tileID
) implements Event, Serializable {}
