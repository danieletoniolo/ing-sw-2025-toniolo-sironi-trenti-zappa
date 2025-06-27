package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to join a lobby.
 *
 * @param nickname The userID of the user joining the lobby
 * @param lobbyID  The ID of the lobby to join
 * @author Vittorio Sironi
 */
public record LobbyJoined(
        String nickname,
        String lobbyID
) implements Event, Serializable {}
