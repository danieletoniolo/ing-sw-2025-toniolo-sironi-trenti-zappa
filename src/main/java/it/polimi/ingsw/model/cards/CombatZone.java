package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.hits.Hit;

import java.util.ArrayList;

/**
 * Represents a combat zone card that causes flight days loss, object loss, and applies hits to the player.
 * This card extends the base Card class and contains information about the penalties
 * incurred when encountering this combat zone during a quest.
 * @author Lorenzo Trenti
 */
public class CombatZone extends Card {
    /** Number of flight days lost when this combat zone is encountered */
    private int flightDays;
    /** Number of objects lost when this combat zone is encountered */
    private int lost;
    /** List of hits that are applied to the player when encountering this combat zone */
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
     * Default constructor for CombatZone.
     * Creates a CombatZone instance with default values.
     */
    public CombatZone(){
        super();
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
