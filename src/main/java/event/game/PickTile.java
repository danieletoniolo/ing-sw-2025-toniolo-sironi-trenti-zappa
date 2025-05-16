package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player picks a tile from the board, reserve or spaceship.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param fromWhere The place from where the tile is picked: 0 = board, 1 = reserve, 2 = spaceship.
 * @param tileID The ID of the tile being picked.
 */
public record PickTile(
        String userID,
        int fromWhere,
        int tileID
) implements Event, Serializable {
}
