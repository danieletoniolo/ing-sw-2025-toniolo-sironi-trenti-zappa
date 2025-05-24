package it.polimi.ingsw.event.game.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This class represents the selection of a planet.
 * @param userID       is the user ID. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param planetNumber is the number of the planet to select.
 */
public record SelectPlanet(
        String userID,
        int planetNumber
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the SelectPlanet it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the SelectPlanet it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<SelectPlanet, T> responder(EventTransceiver transceiver, Function<SelectPlanet, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the SelectPlanet it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SelectPlanet it.polimi.ingsw.event
     */
    public static Requester<SelectPlanet> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
