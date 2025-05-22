package event.game.serverToClient;

import event.eventType.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the score of the game. It is called by the EndState
 * @param playerScores a list of pairs containing the username and the score of each player
 */
public record Score(
        List<Pair<String, Integer>> playerScores
) implements Event, Serializable {
}
