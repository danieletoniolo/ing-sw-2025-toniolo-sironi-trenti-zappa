package event.game.clientToServer;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player has given up.
 * It is used to notify the other players that the player has given up.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 */
public record GiveUp(
        String userID
) implements Event, Serializable {

    /**
     * This method is used to create a responder for the GiveUp event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the GiveUp event.
     */
    public static <T extends Event> Responder<GiveUp, T> responder(EventTransceiver transceiver, Function<GiveUp, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the GiveUp event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the GiveUp event
     */
    public static Requester<GiveUp> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
