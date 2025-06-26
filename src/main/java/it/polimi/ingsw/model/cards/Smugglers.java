package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.good.Good;

import java.util.List;

/**
 * Represents a Smugglers card that extends the base Card class.
 * This card type involves combat with cannon strength requirements and flight day penalties.
 * @author Lorenzo Trenti
 */
public class Smugglers extends Card {
    /** The minimum cannon strength required to successfully complete this smugglers encounter */
    private int cannonStrengthRequired;
    /** The number of flight days lost when engaging with this card */
    private int flightDays;

    /** The list of goods that can be rewarded upon successful completion */
    private List<Good> goodsReward;
    /** The number of goods lost when failing or engaging with this card */
    private int goodsLoss;

    /**
     * Constructor
     * @param ID ID of the card
     * @param goodsReward list of goods rewarded
     * @param goodsLoss number of goods lost
     * @param level level of the card
     * @param cannonStrengthRequired cannon strength of enemies
     * @param flightDays number of flight days lost
     * @throws NullPointerException if goods rewarded == null
     */
    public Smugglers(int level, int ID, List<Good> goodsReward, int goodsLoss, int cannonStrengthRequired, int flightDays) throws NullPointerException {
        super(level, ID);
        this.cannonStrengthRequired = cannonStrengthRequired;
        this.flightDays = flightDays;
        if (goodsReward == null) {
            throw new NullPointerException("Goods reward can't be null");
        }
        this.goodsReward = goodsReward;
        this.goodsLoss = goodsLoss;
    }

    /**
     * Default constructor for Smugglers card.
     * Creates an empty Smugglers card with default values.
     */
    public Smugglers(){
        super();
    }

    /**
     * Get the cannon power required to beat the card
     * @return cannon power required
     */
    public int getCannonStrengthRequired() {
        return cannonStrengthRequired;
    }

    /**
     * Get the number of flight days lost for the quest
     * @return number of flight days lost for the quest
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Get list of good rewarded
     * @return list of good rewarded
     */
    public List<Good> getGoodsReward() {
        return goodsReward;
    }

    /**
     * get number of goods lost
     * @return number of goods lost
     */
    public int getGoodsLoss() {
        return goodsLoss;
    }

    /**
     * Get the card type
     * @return card type
     */
    @Override
    public CardType getCardType() {
        return CardType.SMUGGLERS;
    }

}
