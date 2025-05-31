package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to send the Abandoned Ship card details to the client.
 * It contains the card's ID, level, crew required, flight days, and credit.
 * @param ID    the ID of the card.
 * @param level the level of the card.
 */
public record GetCardStardust(
        int ID,
        int level
) implements Event, Serializable {
}
