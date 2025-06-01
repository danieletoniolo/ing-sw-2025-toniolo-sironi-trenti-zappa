package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.good.Good;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to get the planets card.
 * @param ID         is the ID of the card.
 * @param level      is the level of the card.
 * @param planets    is a list of lists of integers representing the planets and the goods on them.
 * @param flightDays is the number of flight days lost when visiting the planets.
 */
public record GetCardPlanets(
        int ID,
        int level,
        List<List<Integer>> planets,
        int flightDays
) implements Event, Serializable {
}
