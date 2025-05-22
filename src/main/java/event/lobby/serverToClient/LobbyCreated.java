package event.lobby.serverToClient;

import Model.Game.Board.Level;
import event.EventTransceiver;
import event.Requester;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Event to create a new lobby.
 *
 * @param nickname   nickname of the user creating the lobby
 * @param lobbyID    the name of the lobby
 * @param maxPlayers the maximum number of players allowed in the lobby
 * @param level      the level of the game
 */
public record LobbyCreated(
        String nickname,
        String lobbyID,
        int maxPlayers,
        Level level
) implements Event, Serializable {}
