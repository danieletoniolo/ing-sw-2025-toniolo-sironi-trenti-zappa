package event.lobby.serverToClient;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to join a lobby.
 *
 * @param nickname The nickname of the user joining the lobby
 * @param lobbyID  The ID of the lobby to join
 */
public record LobbyJoined(
        String nickname,
        String lobbyID
) implements Event, Serializable {}
