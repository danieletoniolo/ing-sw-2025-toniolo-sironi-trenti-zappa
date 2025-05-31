package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to send the Abandoned Ship card details to the client.
 * @param ID           ID of the card
 * @param level        level of the card
 * @param crewRequired number of crew members required for the quest
 * @param flightDays   number of flight days lost for the quest
 * @param credit       number of credit rewarded
 */
public record GetCardAbandonedShip(
        int ID,
        int level,
        int crewRequired,
        int flightDays,
        int credit
) implements Event, Serializable {
}
