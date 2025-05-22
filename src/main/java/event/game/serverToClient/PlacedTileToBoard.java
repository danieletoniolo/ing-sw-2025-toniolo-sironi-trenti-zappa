package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player place a tile on the board.
 * @param nickname is the nickname of the player who placed the tile.
 */
public record PlacedTileToBoard(
        String nickname
) implements Event, Serializable {}
