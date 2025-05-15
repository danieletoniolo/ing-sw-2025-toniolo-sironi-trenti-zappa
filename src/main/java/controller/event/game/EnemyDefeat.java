package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

/**
 * Use when a player defeats an enemy.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param enemyDefeat is true if the player has defeated an enemy, false otherwise.
 */
public record EnemyDefeat(
        String userID,
        boolean enemyDefeat
) implements Event, Serializable {
}
