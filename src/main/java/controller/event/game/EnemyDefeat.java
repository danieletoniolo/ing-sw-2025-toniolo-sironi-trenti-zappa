package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

public record EnemyDefeat(
        boolean enemyDefeat
) implements Event, Serializable {
}
