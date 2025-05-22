package event.lobby.clientToServer;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to set the nickname of a user.
 * The nickname is used to identify the user in the lobby.
 *
 * @param userID   nickname of the user
 * @param nickname The nickname of the user
 */
public record SetNickname(
        String userID,
        String nickname
)  implements Event, Serializable {
    /**
     * This method is used to create a responder for the SetNickname event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the SetNickname event.
     */
    public static <T extends Event> Responder<SetNickname, T> responder(EventTransceiver transceiver, Function<SetNickname, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the SetNickname event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SetNickname event
     */
    public static Requester<SetNickname> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
