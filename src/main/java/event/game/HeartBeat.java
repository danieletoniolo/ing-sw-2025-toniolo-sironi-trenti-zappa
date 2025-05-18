package event.game;

import event.Event;

import java.io.Serializable;

/**
 * This event is used to check if the client is still connected to the server.
 * The @link{Connection} manage the heart beat, so the view doesn't need to
 */
public record HeartBeat(

) implements Event, Serializable {
}
