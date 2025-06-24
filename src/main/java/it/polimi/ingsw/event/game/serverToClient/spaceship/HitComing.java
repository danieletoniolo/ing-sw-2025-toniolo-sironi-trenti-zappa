package it.polimi.ingsw.event.game.serverToClient.spaceship;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * It is used to notify the client that the previous hit is done ande the next hit come in.
 */
public record HitComing(
        String nickname
) implements Event, Serializable {
}
