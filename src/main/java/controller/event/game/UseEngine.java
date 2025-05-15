package controller.event.game;

import controller.event.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param enginesPowerToUse The power of the engines to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 */
public record UseEngine(
        String userID,
        float enginesPowerToUse,
        ArrayList<Integer> batteriesIDs
) implements Event, Serializable {
}
