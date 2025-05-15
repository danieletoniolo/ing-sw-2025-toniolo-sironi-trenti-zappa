package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

/**
 * This event is sent when the user rotates a tile.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param tileID is the ID of the tile that has been rotated.
 */
public record RotateTile(
        String userID,
        int tileID
) implements Event, Serializable {
}
