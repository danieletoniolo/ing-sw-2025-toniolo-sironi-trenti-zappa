package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.Hit;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

public record GetCardMeteorSwarm(
        int ID,
        int level,
        List<Pair<Integer, Integer>> meteors
) implements Event, Serializable {
}
