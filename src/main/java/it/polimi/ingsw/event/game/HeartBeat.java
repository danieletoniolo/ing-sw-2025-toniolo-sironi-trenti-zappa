package it.polimi.ingsw.event.game;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This it.polimi.ingsw.event is used to check if the client is still connected to the server.
 * The @link{Connection} manage the heart beat, so the it.polimi.ingsw.view doesn't need to
 */
public record HeartBeat(

) implements Event, Serializable {
}
