package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.Hit;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to send the Meteor Swarm card details to the client.
 * It contains the card's ID, level, and a list of meteors.
 * @param ID       ID of the card
 * @param level    level of the card
 * @param meteors  list of meteors associated with the card
 * @author Vittorio Sironi
 */
public record GetCardMeteorSwarm(
        int ID,
        int level,
        List<Pair<Integer, Integer>> meteors
) implements Event, Serializable {
}
