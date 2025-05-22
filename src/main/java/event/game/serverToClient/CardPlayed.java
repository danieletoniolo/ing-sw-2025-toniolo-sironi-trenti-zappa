package event.game.serverToClient;

import event.eventType.Event;

import java.io.Serializable;

/**
 * This event is used when a player have played a card.
 */
public record CardPlayed(

) implements Event, Serializable {
}

