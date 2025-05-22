package event.lobby;

import event.eventType.Event;

import java.io.Serializable;

/**
 * This event is used when a lobby is removed.
 * @param lobbyID is the ID of the lobby to be removed
 */
public record RemoveLobby(
    String lobbyID
) implements Event, Serializable {
}
