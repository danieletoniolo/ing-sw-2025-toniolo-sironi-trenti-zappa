package event.lobby.serverToClient;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to leave a lobby.
 *
 * @param nickname nickname of the user leaving the lobby
 * @param lobbyID  The lobbyID is the lobbyID of the lobby the user is leaving.
 */
public record LobbyLeft(
        String nickname,
        String lobbyID
) implements Event, Serializable {}
