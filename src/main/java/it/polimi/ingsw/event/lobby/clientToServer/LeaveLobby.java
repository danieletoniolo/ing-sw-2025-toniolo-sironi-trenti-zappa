package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.StatusEvent;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to leave a lobby.
 *
 * @param userID  userID of the user leaving the lobby
 * @param lobbyID The lobbyID is the lobbyID of the lobby the user is leaving.
 */
public record LeaveLobby(
        String userID,
        String lobbyID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the LeaveLobby event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the LeaveLobby event.
     */
    public static Responder<LeaveLobby> responder(EventTransceiver transceiver, Function<LeaveLobby, StatusEvent> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the LeaveLobby event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the LeaveLobby event
     */
    public static Requester<LeaveLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
