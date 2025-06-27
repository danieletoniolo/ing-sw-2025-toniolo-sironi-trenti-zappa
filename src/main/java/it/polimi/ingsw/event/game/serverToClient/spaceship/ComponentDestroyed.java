package it.polimi.ingsw.event.game.serverToClient.spaceship;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used when a player have to destroy components of his ship.
 * @param nickname            The userID of the player who has to destroy the components.
 * @param destroyedComponents The list of components that the player has to destroy, the pair represent the row and the columns of the component
 * @author Vittorio Sironi
 */
public record ComponentDestroyed(
        String nickname,
        List<Pair<Integer, Integer>> destroyedComponents
) implements Event, Serializable {
}
