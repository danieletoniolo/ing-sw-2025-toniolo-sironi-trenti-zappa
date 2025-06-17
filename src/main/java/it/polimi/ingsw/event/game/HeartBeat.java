package it.polimi.ingsw.event.game;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This is the {@link Event} is used to check if the client is still connected to the server.
 * The {@link it.polimi.ingsw.network.Connection} manage the heart beat, so the view doesn't need to
 */
public record HeartBeat(

) implements Event, Serializable {
}
