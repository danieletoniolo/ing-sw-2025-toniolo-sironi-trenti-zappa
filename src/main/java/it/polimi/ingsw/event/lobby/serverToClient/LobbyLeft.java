package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to leave a lobby.
 *
 * @param nickname userID of the user leaving the lobby
 * @param lobbyID  The lobbyID is the lobbyID of the lobby the user is leaving.
 * @author Vittorio Sironi
 */
public record LobbyLeft(
        String nickname,
        String lobbyID
) implements Event, Serializable {}
