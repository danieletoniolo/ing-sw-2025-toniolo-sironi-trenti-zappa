package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player place a tile on the board.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param fromWhere The place from where the tile is picked: 0 = board, 1 = reserve, 2 = spaceship.
 * @param row The row of the tile on the board.
 * @param col The column of the tile on the board.
 */
public record PlaceTile(
        String userID,
        int fromWhere,
        int row,
        int col
) implements Event, Serializable {
}
