package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event sent by the server to notify that a player is ready to start the game.
 * This event is used in the lobby phase when players indicate they are ready.
 * @param nickname the nickname of the player who is ready
 * @param isReady  true if the player is ready, false otherwise
 * @author Daniele Toniolo
 */
public record ReadyPlayer(
        String nickname,
        boolean isReady
) implements Event, Serializable {
}
