package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.Event;

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
     * This method is used to create a responder for the SetNickname it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the SetNickname it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<SetNickname, T> responder(EventTransceiver transceiver, Function<SetNickname, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the SetNickname it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the SetNickname it.polimi.ingsw.event
     */
    public static Requester<SetNickname> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
