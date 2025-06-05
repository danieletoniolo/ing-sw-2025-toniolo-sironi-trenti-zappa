package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.TransmitterEventWrapper;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to join a lobby.
 *
 * @param userID  userID of the user joining the lobby
 * @param lobbyID The ID of the lobby to join
 */
public record JoinLobby(
        String userID,
        String lobbyID
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the JoinLobby event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the JoinLobby event.
     */
    public static Responder<JoinLobby> responder(EventTransceiver transceiver, Function<JoinLobby, TransmitterEventWrapper> response) {
        Responder<JoinLobby> responder =  new Responder<>(transceiver);
        responder.registerListener(response);
        return responder;
    }

    /**
     * Creates a Requester for the JoinLobby event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the JoinLobby event
     */
    public static Requester<JoinLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
