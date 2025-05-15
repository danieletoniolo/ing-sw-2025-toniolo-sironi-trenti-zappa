package controller.event.game;

import controller.event.Event;

import java.io.Serializable;

/**
 * Use when a player receives coins.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param coins The number of coins received by the player.
 */
public record AddCoins(
        String userID,
        int coins
) implements Event, Serializable {
}
