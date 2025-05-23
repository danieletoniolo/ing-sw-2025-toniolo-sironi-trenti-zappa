package event.lobby.clientToServer;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a lobby is removed.
 * @param lobbyID is the ID of the lobby to be removed
 */
public record RemoveLobby(
    String lobbyID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the RemoveLobby event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the RemoveLobby event.
     */
    public static <T extends Event> Responder<RemoveLobby, T> responder(EventTransceiver transceiver, Function<RemoveLobby, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the RemoveLobby event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the RemoveLobby event
     */
    public static Requester<RemoveLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
