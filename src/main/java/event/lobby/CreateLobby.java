package event.lobby;

import Model.Game.Board.Level;
import event.eventType.Event;

import java.io.Serializable;

/**
 * Event to create a new lobby.
 *
 * @param userID     The nickname is the user username when the event is sent to the other client.
 *                   The nickname is the UUID when the event is sent from the client to the server.
 *                   In this way other client cannot fake to be another client, because the UUID is known only by the correct client
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
}
