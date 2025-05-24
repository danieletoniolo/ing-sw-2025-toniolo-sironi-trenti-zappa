package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * This it.polimi.ingsw.event is used when a player has used the cannons.
 * It is used to notify the other players that the player has used the cannons.
 * @param userID       is the user ID. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param cannonsIDs   The IDs of the cannons to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 * */
public record UseCannons(
        String userID,
        List<Integer> cannonsIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the UseCannons it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the UseCannons it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<UseCannons, T> responder(EventTransceiver transceiver, Function<UseCannons, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the UseCannons it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the UseCannons it.polimi.ingsw.event
     */
    public static Requester<UseCannons> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
