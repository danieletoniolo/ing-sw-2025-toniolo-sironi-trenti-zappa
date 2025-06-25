package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the score of the game. It is called by the EndState
 * @param playerScores a list of pairs containing the username and the score of each player
 * @param rewardPhase the phase of the reward (0 for finish order, 1 for best looking ship, 2 for sale of goods, 3 for losses, 4 for leave game)
 */
public record Score(
        List<Pair<String, Integer>> playerScores,
        int rewardPhase
) implements Event, Serializable {
}
