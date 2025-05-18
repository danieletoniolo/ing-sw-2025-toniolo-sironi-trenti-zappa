package event.game;

import event.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the score of the game. It is called by the EndState
 * @param playerScores a list of pairs containing the username and the score of each player
 */
public record Score(
        ArrayList<Pair<String, Integer>> playerScores
) implements Event, Serializable {
}
