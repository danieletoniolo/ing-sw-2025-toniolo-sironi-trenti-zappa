package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This it.polimi.ingsw.event is used when a player have to choose the fragments of the ship. So it returns the fragments from which the user can choose
 * @param nickname  is the nickname of the player. Only the user know his ID, so the it.polimi.ingsw.event is not faked.
 * @param fragments The list of fragments that the player has to choose, the pair represent the row and the columns of the components that are in the fragment
 */
public record Fragments(
        String nickname,
        List<List<Pair<Integer, Integer>>> fragments
) implements Event, Serializable {}
