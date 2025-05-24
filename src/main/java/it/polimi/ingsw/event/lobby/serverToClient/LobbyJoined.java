package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

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
