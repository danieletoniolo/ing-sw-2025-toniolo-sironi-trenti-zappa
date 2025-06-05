package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event sent by the server to notify that a player has given up.
 * It contains the nickname of the player who gave up.
 *
 * @param nickname the nickname of the player who gave up
*/
public record PlayerGaveUp(
    String nickname
) implements Event, Serializable {
}
