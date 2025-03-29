package Model.Cards;

import Model.Good.Good;

import java.util.List;

public class Smugglers extends Card {
    private int cannonStrengthRequired;
    private int flightDays;

    private List<Good> goodsReward;
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
