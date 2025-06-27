package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to send the smugglers card details to the client.
 * @param ID                     the ID of the card.
 * @param level                  the level of the card.
 * @param cannonStrengthRequired the cannon strength required to play the card.
 * @param flightDays             the number of flight days required to play the card.
 * @param goodsReward            list of goods rewarded
 * @param goodsLoss              number of goods lost
 * @author Vittorio Sironi
 */
public record GetCardSmugglers(
        int ID,
        int level,
        int cannonStrengthRequired,
        int flightDays,
        List<Integer>goodsReward,
        int goodsLoss
) implements Event, Serializable {
}
