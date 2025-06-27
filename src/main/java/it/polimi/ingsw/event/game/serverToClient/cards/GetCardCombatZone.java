package it.polimi.ingsw.event.game.serverToClient.cards;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This event is used to send the combat zone card information to the client.
 * It contains the ID of the card, its level, flight days, lost status, and a list of fires.
 * @param ID         the ID of the card
 * @param level      the level of the card
 * @param flightDays the number of flight days
 * @param lost       indicates if the card is lost
 * @param fires      a list of integers representing the fires on the card
 * @author Vittorio Sironi
 */
public record GetCardCombatZone(
        int ID,
        int level,
        int flightDays,
        int lost,
        List<Pair<Integer, Integer>> fires
) implements Event, Serializable {
}
