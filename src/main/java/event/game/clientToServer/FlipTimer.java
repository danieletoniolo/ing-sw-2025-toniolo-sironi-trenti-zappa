package event.game.clientToServer;

import event.Requester;
import event.eventType.Event;
import event.EventTransceiver;
import event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when the timer is flipped.
 * It is used to notify the other players that the timer has been flipped.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 */
public record FlipTimer(
        String userID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the FlipTimer event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the FlipTimer event.
     */
    public static <T extends Event> Responder<FlipTimer, T> responder(EventTransceiver transceiver, Function<FlipTimer, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the FlipTimer event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the FlipTimer event
     */
    public static Requester<FlipTimer> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
