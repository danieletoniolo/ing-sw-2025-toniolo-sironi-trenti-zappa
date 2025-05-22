package event.game.serverToClient;

import event.eventType.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This event is used when a player have to destroy components of his ship.
 * @param nickname            The nickname of the player who has to destroy the components.
 * @param destroyedComponents The list of components that the player has to destroy, the pair represent the row and the columns of the component
 */
public record ComponentDestroyed(
        String nickname,
        List<Pair<Integer, Integer>> destroyedComponents
) implements Event, Serializable {
}
