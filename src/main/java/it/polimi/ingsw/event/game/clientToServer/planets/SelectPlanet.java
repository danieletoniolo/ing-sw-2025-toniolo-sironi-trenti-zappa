package it.polimi.ingsw.event.game.clientToServer.planets;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This class represents the selection of a planet.
 * @param userID       is the user ID. Only the user know his ID, so the event is not faked.
 * @param planetNumber is the number of the planet to select.
 */
public record SelectPlanet(
        String userID,
        int planetNumber
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the SelectPlanet event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the SelectPlanet event.
     */
    public static Responder<SelectPlanet> responder(EventTransceiver transceiver, Function<SelectPlanet, StatusEvent> response) {
        Responder<SelectPlanet> responder =  new Responder<>(transceiver);
        responder.registerListenerStatus(response);
        return responder;
    }

    /**
     * Creates a Requester for the SelectPlanet event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SelectPlanet event
     */
    public static Requester<SelectPlanet> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
