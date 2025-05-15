package controller.event.lobby;

import controller.event.Event;

import java.io.Serializable;

/**
 * Event to leave a lobby.
 * It is not passed the lobby because the user can be only in one lobby at the time.
 * Therefor the client knows the lobbyID of the lobby he is leaving.
 *
 * @param userID The userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 */
public record LeaveLobby(
        String userID
) implements Event, Serializable {
}
