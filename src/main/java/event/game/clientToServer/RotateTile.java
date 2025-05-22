package event.game.clientToServer;

import event.Requester;
import event.eventType.Event;
import event.EventTransceiver;
import event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is sent when the user rotates a tile.
 * @param userID is the user ID. Only the user know his ID, so the event is not faked.
 * @param tileID is the ID of the tile that has been rotated.
 */
public record RotateTile(
        String userID,
        int tileID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the RotateTile event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the RotateTile event.
     */
    public static <T extends Event> Responder<RotateTile, T> responder(EventTransceiver transceiver, Function<RotateTile, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the RotateTile event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the RotateTile event
     */
    public static Requester<RotateTile> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
