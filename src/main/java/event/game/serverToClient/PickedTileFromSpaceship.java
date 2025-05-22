package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player pick a tile from the spaceship.
 * @param nickname is the nickname of the player that pick the tile.
 */
public record PickedTileFromSpaceship(
        String nickname
) implements Event, Serializable {}
