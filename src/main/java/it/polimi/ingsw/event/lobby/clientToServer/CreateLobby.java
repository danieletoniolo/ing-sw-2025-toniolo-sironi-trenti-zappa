package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Responder;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to create a new lobby.
 *
 * @param userID     userID of the user creating the lobby
 * @param maxPlayers the maximum number of players allowed in the lobby
 * @param level      the level of the game
 */
public record CreateLobby(
        String userID,
        int maxPlayers,
        Integer level
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the CreateLobby event.
     * @param transceiver is the EventTransceiver that will be used to send the event.
     * @param response    is the function that will be used to create the response event.
     * @return            a Responder for the CreateLobby event.
     */
    public static <T extends Event> Responder<CreateLobby, T> responder(EventTransceiver transceiver, Function<CreateLobby, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the CreateLobby event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the CreateLobby event
     */
    public static Requester<CreateLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
