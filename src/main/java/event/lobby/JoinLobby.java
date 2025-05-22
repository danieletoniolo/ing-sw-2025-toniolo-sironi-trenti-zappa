package event.lobby;

import event.eventType.Event;

import java.io.Serializable;

/**
 * Event to join a lobby.
 *
 * @param userID  The nickname is the user username when the event is sent to the other client.
 *                The nickname is the UUID when the event is sent from the client to the server.
 *                In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param lobbyID The ID of the lobby to join
 */
public record JoinLobby(
        String userID,
        String lobbyID
) implements Event, Serializable {
}
