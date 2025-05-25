package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to create a new lobby.
 *
 * @param nickname   userID of the user creating the lobby
 * @param lobbyID    the name of the lobby
 * @param maxPlayers the maximum number of players allowed in the lobby
 * @param level      the level of the game
 */
public record LobbyCreated(
        String nickname,
        String lobbyID,
        int maxPlayers,
        Integer level
) implements Event, Serializable {}
