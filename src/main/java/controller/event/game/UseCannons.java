package controller.event.game;

import Model.Player.PlayerColor;
import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player has used the cannons.
 * It is used to notify the other players that the player has used the cannons.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param cannonsPowerToUse The power of the cannons to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 * */
public record UseCannons(
        String userID,
        float cannonsPowerToUse,
        ArrayList<Integer> batteriesIDs
) implements Event, Serializable {
}
