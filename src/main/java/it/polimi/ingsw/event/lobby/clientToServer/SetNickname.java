package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.*;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to set the userID of a user.
 * The userID is used to identify the user in the lobby.
 *
 * @param userID   userID of the user
 * @param nickname The userID of the user
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
