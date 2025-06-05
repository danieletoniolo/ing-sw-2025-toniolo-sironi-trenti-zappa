package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Represents an event used to set the unique user identifier (userID) of a user.
 *
 * The userID is generally a distinct string used to identify the user within the system.
 * It can be used across different contexts or events to associate actions or states with a specific user.
 *
 * @param userID the unique identifier for the user
 */
public record UserIDSet(
        String userID
) implements Event, Serializable {
}
