package it.polimi.ingsw.event.game.serverToClient.spaceship;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.player.PlayerData;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to notify the client about the invalid components of a spaceship.
 * It contains the nickname of the player and a list of pairs representing the invalid components.
 * Each pair consists of two integers: row and column indices of the invalid component in the spaceship grid.
 * If the ship has no invalid components, the list will be empty.
 *
 * @param nickname            The nickname of the player whose spaceship has invalid components.
 * @param invalidComponents   A list of pairs representing the invalid components.
 * @author Vittorio Sironi
 */
public record InvalidComponents(
        String nickname,
        List<Pair<Integer, Integer>> invalidComponents
) implements Event, Serializable {
}
