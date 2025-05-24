package it.polimi.ingsw.event.game;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This class is used to wrap an it.polimi.ingsw.event with an ID. It is used to send events
 * @param ID the ID of the it.polimi.ingsw.event
 * @param event the it.polimi.ingsw.event to be sent
 * @param <T> the type of the it.polimi.ingsw.event
 */
public record EventWrapper<T extends Event>(
        int ID,
        T event
) implements Event, Serializable {
}
