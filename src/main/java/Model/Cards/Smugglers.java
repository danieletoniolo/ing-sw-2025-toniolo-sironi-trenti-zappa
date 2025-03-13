package Model.Cards;

import Model.Good.Good;
import Model.Player.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class Smugglers extends Enemies {
    private List<Good> goodsReward;
    private int goodsLoss;

    /**
     *
     * @param goodsReward list of goods rewarded
     * @param goodsLoss number of goods lost
     * @param level level of the card
     * @param cannonStrength cannon strength of enemies
     * @param flightDays number of flight days lost
     * @throws NullPointerException if goods rewarded == null
     */
    public Smugglers(List<Good> goodsReward, int goodsLoss, int level, int cannonStrength, int flightDays) throws NullPointerException {
        super(level, cannonStrength, flightDays);
        if (goodsReward == null) {
            throw new NullPointerException("Goods reward can't be null");
        }
        this.goodsReward = goodsReward;
        this.goodsLoss = goodsLoss;
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

    @Override
    public void entry(ArrayList<PlayerData> players) {
        //TODO
    }

    @Override
    public void execute(PlayerData player) {
        //TODO

    }

    @Override
    public void exit() {
        //TODO
    }
}
