package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to send the Epidemic card details to the client.
 * It contains the card's ID, level, crew required, flight days, and credit.
 * @param ID    ID of the card
 * @param level level of the card
 * @author Vittorio Sironi
 */
public record GetCardEpidemic(
        int ID,
        int level
) implements Event, Serializable {
}
