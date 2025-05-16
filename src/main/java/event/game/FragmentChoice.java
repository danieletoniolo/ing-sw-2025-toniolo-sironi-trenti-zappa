package event.game;

import event.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This event is used when a player have to choose the fragments of the ship. So it returns the fragments from which the user can choose
 * @param userID is the user username when the event is sent to the other client.
 *               The userID is the UUID when the event is sent from the client to the server.
 *               In this way other client cannot fake to be another client, because the UUID is known only by the correct client
 * @param fragments The list of fragments that the player has to choose, the pair represent the row and the columns of the components that are in the fragment
 */
public record FragmentChoice(
        String userID,
        ArrayList<ArrayList<Pair<Integer, Integer>>> fragments
) implements Event, Serializable {
}
