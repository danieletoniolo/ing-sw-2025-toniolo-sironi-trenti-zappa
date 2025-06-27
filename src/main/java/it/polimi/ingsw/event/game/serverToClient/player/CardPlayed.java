package it.polimi.ingsw.event.game.serverToClient.player;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * This class represents the event of a player playing a card.
 * It contains the nickname of the player who played the card.
 *
 * @param nickname The nickname of the player who played the card.ù
 * @author Vittorio Sironi
 */
public record CardPlayed(
        String nickname
) implements Event, Serializable {
}
