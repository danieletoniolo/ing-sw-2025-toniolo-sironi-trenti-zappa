package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * This event is used when a player have played a card.
 */
public record CardPlayed(

) implements Event, Serializable {
}

