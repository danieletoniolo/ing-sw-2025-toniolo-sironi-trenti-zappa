package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Event to get the Abandoned Station card details.
 * @param level        level of the card
 * @param ID           ID of the card
 * @param crewRequired number of crew members required for the quest
 * @param flightDays   number of flight days lost for the quest
 * @param goods        list of goods rewarded
 */
public record GetCardAbandonedStation(
        int level,
        int ID,
        int crewRequired,
        int flightDays,
        List<Integer>goods
) implements Event, Serializable {
}
