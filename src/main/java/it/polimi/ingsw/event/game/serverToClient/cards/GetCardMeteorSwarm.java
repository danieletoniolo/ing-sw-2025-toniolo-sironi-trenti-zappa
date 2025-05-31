package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.Hit;

import java.io.Serializable;
import java.util.List;

public record GetCardMeteorSwarm(
        int ID,
        int level,
        List<Integer> meteors
) implements Event, Serializable {
}
