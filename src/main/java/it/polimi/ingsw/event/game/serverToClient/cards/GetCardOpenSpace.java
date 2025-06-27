package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This event is used to send the OpenSpace card details to the client.
 * @param ID    the ID of the card
 * @param level the level of the card
 * @author Vittorio Sironi
 */
public record GetCardOpenSpace(
        int ID,
        int level
) implements Event, Serializable {
}
