package it.polimi.ingsw.event.type;

/**
 * Interface representing a status event that extends the base Event interface.
 * This interface defines the contract for events that carry status information
 * and are associated with a specific user.
 * @author Vittorio Sironi
 */
public interface StatusEvent extends Event {

    /**
     * Gets the status information carried by this event.
     *
     * @return a String containing the status data
     */
    String get();

    /**
     * Gets the identifier of the user associated with this status event.
     *
     * @return a String containing the user ID
     */
    String getUserID();
}
