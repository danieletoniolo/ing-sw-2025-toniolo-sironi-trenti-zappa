package it.polimi.ingsw.event.game.serverToClient.dice;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event sent by the server to the client when a player rolls the dice.
 * It contains the nickname of the player and the values of the two dice rolled.
 * @param nickname   the nickname of the player who rolled the dice
 * @param diceValue1 the value of the first die
 * @param diceValue2 the value of the second die
 * @author Daniele Toniolo
 */
public record DiceRolled(
        String nickname,
        int diceValue1,
        int diceValue2
) implements Event, Serializable {
}
