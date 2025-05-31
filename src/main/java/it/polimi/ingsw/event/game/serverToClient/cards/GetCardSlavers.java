package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to send the v card details to the client.
 * @param ID                     the ID of the card.
 * @param level                  the level of the card.
 * @param cannonStrengthRequired the cannon strength required to play the card.
 * @param flightDays             the number of flight days required to play the card.
 * @param crewLost               the number of crew members lost when playing the card.
 * @param credit                 the credit gained when playing the card.
 */
public record GetCardSlavers(
        int ID,
        int level,
        int cannonStrengthRequired,
        int flightDays,
        int crewLost,
        int credit
) implements Event, Serializable {
}
