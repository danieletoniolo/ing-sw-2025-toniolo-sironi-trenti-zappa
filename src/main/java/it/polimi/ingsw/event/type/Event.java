package it.polimi.ingsw.event.type;

import java.io.Serializable;

/**
 * Represents a general it.polimi.ingsw.event that can be transmitted within the system.
 * This interface serves as a marker for all types of events that are serializable,
 * allowing for consistent handling of events in the application.
 *
 * Classes implementing the Event interface can define specific types of events
 * and behaviors associated with those events, which can then be transmitted or
 * handled by relevant components.
 */
public interface Event extends Serializable {
}
