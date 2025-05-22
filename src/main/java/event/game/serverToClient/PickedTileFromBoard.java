package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player pick a tile from the board.
 * @param nickname is the nickname of the player who picked the tile.
 * @param tileID   The ID of the tile being picked.
 */
public record PickedTileFromBoard(
        String nickname,
        int tileID
) implements Event, Serializable {}
