package Model.Cards;

import Model.Cards.Hits.Hit;

import java.util.ArrayList;
import java.util.List;

public class CombatZone extends Card {
    private int flightDays;
    private int lost;
    private ArrayList<Hit> fires;

    /**
     *
     * @param flightDays number of flight days lost for the quest
     * @param lost number of objects lost (varies depending on level card)
     * @param fires list of Hits
     * @param level level of the card
     * @param ID ID of the card
     * @throws NullPointerException if fires == null
     */
    public CombatZone(int flightDays, int lost, ArrayList<Hit> fires, int level, int ID) throws NullPointerException{
        super(level, ID);
        this.flightDays = flightDays;
        this.lost = lost;

        if (fires == null || fires.isEmpty()) {
            throw new NullPointerException("Fires can't be null");
        }
        this.fires = fires;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get number of objects lost
     * @return number of objects lost
     */
    public int getLost() {
        return lost;
    }

    /**
     * Get the list of hits
     * @return list of hits
     */
    public ArrayList<Hit> getFires() {
        return fires;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.COMBATZONE;
    }

}
