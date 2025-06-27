package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;
import java.io.Serializable;

/**
 * Use when a player receives coins.
 * @param nickname is the userID of the player who receives the coins.
 * @param coins    The number of coins received by the player.
 * @author Vittorio Sironi
 */
public record UpdateCoins(
        String nickname,
        int coins
) implements Event, Serializable {
}
