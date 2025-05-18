package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player has lost the game.
 * It is used to notify the other players that the player has lost.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 */
public record PlayerLose(
        String userID
) implements Event, Serializable {
}
