package event.lobby.serverToClient;

import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a lobby is removed.
 * @param lobbyID is the ID of the lobby to be removed
 */
public record LobbyRemoved(
    String lobbyID
) implements Event, Serializable {}
