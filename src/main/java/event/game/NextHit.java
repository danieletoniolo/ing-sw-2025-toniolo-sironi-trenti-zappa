package event.game;

import event.Event;

import java.io.Serializable;

/**
 * It is used to notify the client that the previous hit is done ande the next hit come in.
 */
public record NextHit() implements Event, Serializable {
}
