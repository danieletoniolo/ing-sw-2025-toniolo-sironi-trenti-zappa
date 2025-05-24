package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This it.polimi.ingsw.event is used when a player have to choose the fragments of the ship. So it returns the fragments from which the user can choose
 * @param userID         is the user ID. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param fragmentChoice The list of fragments that the player has to choose, the pair represent the row and the columns of the components that are in the fragment
 */
public record ChooseFragment(
        String userID,
        int fragmentChoice
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the FragmentChoice it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the FragmentChoice it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<ChooseFragment, T> responder(EventTransceiver transceiver, Function<ChooseFragment, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the ChooseFragment it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the ChooseFragment it.polimi.ingsw.event
     */
    public static Requester<ChooseFragment> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
