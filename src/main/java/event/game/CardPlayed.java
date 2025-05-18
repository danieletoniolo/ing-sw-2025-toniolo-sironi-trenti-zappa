package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used when a player have played a card.
 */
public record CardPlayed(

) implements Event, Serializable {
}

