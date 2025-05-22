package event.game.clientToServer;

import event.Requester;
import event.eventType.Event;
import event.EventTransceiver;
import event.Responder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param userID       is the user ID. Only the user know his ID, so the event is not faked.
 * @param enginesIDs   The IDs of the engines to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 */
public record UseEngines(
        String userID,
        List<Integer> enginesIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the UseEngines event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the UseEngines event.
     */
    public static <T extends Event> Responder<UseEngines, T> responder(EventTransceiver transceiver, Function<UseEngines, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the UseEngines event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the UseEngines event
     */
    public static Requester<UseEngines> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
