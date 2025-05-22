package event.lobby.serverToClient;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to set the nickname of a user.
 * The nickname is used to identify the user in the lobby.
 *
 * @param nickname The nickname of the user
 */
public record NicknameSet(
        String nickname
)  implements Event, Serializable {}
