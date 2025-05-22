package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * This event is used when a player have to choose the fragments of the ship. So it returns the fragments from which the user can choose
 * @param nickname  is the nickname of the player. Only the user know his ID, so the event is not faked.
 * @param fragments The list of fragments that the player has to choose, the pair represent the row and the columns of the components that are in the fragment
 */
public record Fragments(
        String nickname,
        List<List<Pair<Integer, Integer>>> fragments
) implements Event, Serializable {}
