package it.polimi.ingsw.event.game.serverToClient.timer;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is sent when the last timer finishes.
 * It is used to notify the clients that the game has ended.
 */
public record LastTimerFinished(

) implements Event, Serializable {
}
