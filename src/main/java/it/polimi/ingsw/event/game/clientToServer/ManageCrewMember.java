package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This class represents the manage crew member it.polimi.ingsw.event.
 * @param userID   is the user ID. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param mode     is the mode of the it.polimi.ingsw.event. 0 = add, 1 = remove
 * @param crewType is the type of the crew member. 0 = crew, 1 = brown alien, 2 = purple alien
 * @param cabinID  ID of the cabin where the crew member is added or removed
 */
public record ManageCrewMember(
        String userID,
        int mode,
        int crewType,
        int cabinID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the ManageCrewMember it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the ManageCrewMember it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<ManageCrewMember, T> responder(EventTransceiver transceiver, Function<ManageCrewMember, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the ManageCrewMember it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the ManageCrewMember it.polimi.ingsw.event
     */
    public static Requester<ManageCrewMember> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
