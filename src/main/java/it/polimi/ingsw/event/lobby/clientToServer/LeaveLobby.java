package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to leave a lobby.
 *
 * @param userID  nickname of the user leaving the lobby
 * @param lobbyID The lobbyID is the lobbyID of the lobby the user is leaving.
 */
public record LeaveLobby(
        String userID,
        String lobbyID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the LeaveLobby it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the LeaveLobby it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<LeaveLobby, T> responder(EventTransceiver transceiver, Function<LeaveLobby, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the LeaveLobby it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the LeaveLobby it.polimi.ingsw.event
     */
    public static Requester<LeaveLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
