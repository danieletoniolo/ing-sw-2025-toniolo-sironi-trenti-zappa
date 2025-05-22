package event.game.serverToClient;

import event.eventType.Event;

import java.io.Serializable;

/**
 * Use when a player defeats an enemy.
 * @param nickname    is the nickname of the player that has defeated the enemy
 * @param enemyDefeat is true if the player has defeated an enemy, false if the player has less power than it is required, null if it has the same power as required
 */
public record EnemyDefeat(
        String nickname,
        Boolean enemyDefeat
) implements Event, Serializable {
}
