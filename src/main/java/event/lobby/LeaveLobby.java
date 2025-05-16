package event.lobby;

import event.Event;

import java.io.Serializable;

/**
 * Event to leave a lobby.
 *
 * @param userID The userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param lobbyID The lobbyID is the lobbyID of the lobby the user is leaving.
 */
public record LeaveLobby(
        String userID,
        String lobbyID
) implements Event, Serializable {
}
