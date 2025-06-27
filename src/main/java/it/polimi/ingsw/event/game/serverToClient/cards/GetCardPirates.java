package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.cards.hits.Hit;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to send the Card Pirates details to the client.
 * It contains the card's ID, level, crew required, flight days, and credit.
 * @param ID                     the unique identifier of the card
 * @param level                  the level of the card
 * @param cannonStrengthRequired the required cannon strength to defeat the card
 * @param flightDays             the number of flight days lost when facing the card
 * @param fires                  a list of pairs representing the fire hits (hit type and direction)
 * @param credit                 the credit rewarded for completing the card
 * @author Vittorio Sironi
 */
public record GetCardPirates(
        int ID,
        int level,
        int cannonStrengthRequired,
        int flightDays,
        List<Pair<Integer, Integer>> fires,
        int credit
) implements Event, Serializable {
}
