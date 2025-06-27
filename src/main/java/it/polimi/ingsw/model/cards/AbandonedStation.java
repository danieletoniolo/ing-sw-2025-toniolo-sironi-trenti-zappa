package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.good.Good;

import java.util.List;

/**
 * Represents an Abandoned Station card in the game.
 * This card requires crew members and flight days to complete a quest,
 * and rewards the player with a list of goods upon completion.
 * @author Lorenzo Trenti
 */
public class AbandonedStation extends Card {
    /** Number of crew members required for the quest and not lost */
    private int crewRequired;
    /** Number of flight days lost for the quest */
    private int flightDays;
    /** List of goods rewarded upon quest completion */
    private List<Good> goods;

    /**
     *
     * @param level level of the card
     * @param ID ID of the card
     * @param crewRequired number of crew member required for the quest and not lost
     * @param flightDays number of flight days lost for the quest
     * @param goods list of goods rewarded
     * @throws NullPointerException if goods == null
     */
    public AbandonedStation(int level, int ID, int crewRequired, int flightDays, List<Good> goods) throws NullPointerException{
        super(level, ID);
        this.crewRequired = crewRequired;
        this.flightDays = flightDays;

        if (goods == null || goods.isEmpty()) {
            throw new NullPointerException("Good list is null or empty");
        }
        this.goods = goods;
    }

    /**
     * Default constructor for AbandonedStation.
     * Creates an AbandonedStation with default values.
     */
    public AbandonedStation(){
        super();
    }

    /**
     * Get the number of crew members required for the quest
     * @return number of crew members required for the quest
     */
    public int getCrewRequired() {
        return crewRequired;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get the list of goods rewarded
     * @return list of goods rewarded
     */
    public List<Good> getGoods() {
        return goods;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.ABANDONEDSTATION;
    }

}
