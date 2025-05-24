package it.polimi.ingsw.event.lobby.clientToServer;

import it.polimi.ingsw.model.game.board.Level;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to create a new lobby.
 *
 * @param userID     nickname of the user creating the lobby
 * @param lobbyID    the name of the lobby
 * @param maxPlayers the maximum number of players allowed in the lobby
 * @param level      the level of the game
 */
public record CreateLobby(
        String userID,
        String lobbyID,
        int maxPlayers,
        Level level
) implements Event, Serializable {
    /**
     * This method is used to create a responder for the CreateLobby it.polimi.ingsw.event.
     * @param transceiver is the EventTransceiver that will be used to send the it.polimi.ingsw.event.
     * @param response    is the function that will be used to create the response it.polimi.ingsw.event.
     * @return            a Responder for the CreateLobby it.polimi.ingsw.event.
     */
    public static <T extends Event> Responder<CreateLobby, T> responder(EventTransceiver transceiver, Function<CreateLobby, T> response) {
        return new Responder<>(transceiver, response);
    }

    /**
     * Creates a Requester for the CreateLobby it.polimi.ingsw.event.
     *
     * @param transceiver the EventTransceiver that will be used to send and receive events
     * @param lock        the object used to synchronize and manage responses
     * @return a Requester for the CreateLobby it.polimi.ingsw.event
     */
    public static Requester<CreateLobby> requester(EventTransceiver transceiver, Object lock) {
        return new Requester<>(transceiver, lock);
    }
}
