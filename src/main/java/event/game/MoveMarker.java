package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player have to move the marker on the board.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param steps The new position of the marker on the board. It is not the number of the steps to add to the current position
 */
public record MoveMarker(
        String userID,
        int steps
) implements Event, Serializable {
}
