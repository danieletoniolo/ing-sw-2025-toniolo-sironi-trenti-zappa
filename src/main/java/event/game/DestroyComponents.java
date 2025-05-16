package event.game;

import event.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player have to destroy components of his ship.
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param destroyedComponents The list of components that the player has to destroy, the pair represent the row and the columns of the component
 */
public record DestroyComponents(
        String userID,
        ArrayList<Pair<Integer, Integer>> destroyedComponents
) implements Event, Serializable {
}
