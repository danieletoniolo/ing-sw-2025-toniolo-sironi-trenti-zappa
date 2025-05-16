package event.lobby;

import event.Event;

import java.io.Serializable;

/**
 * Event to set the nickname of a user.
 * The nickname is used to identify the user in the lobby.
 *
 * @param userID   The userID is the user username when the event is sent to the other client.
 *                 The userID is the UUID when the event is sent from the client to the server.
 *                 In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param nickname The nickname of the user
 */
public record SetNickname(
        String userID,
        String nickname
)  implements Event, Serializable {
}
