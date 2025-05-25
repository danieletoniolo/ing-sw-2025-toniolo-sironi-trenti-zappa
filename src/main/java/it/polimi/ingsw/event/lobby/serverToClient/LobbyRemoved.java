package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used when a lobby is removed.
 * @param lobbyID is the ID of the lobby to be removed
 */
public record LobbyRemoved(
    String lobbyID
) implements Event, Serializable {}
