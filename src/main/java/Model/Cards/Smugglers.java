package Model.Cards;

import Model.Good.Good;

import java.util.List;

public class Smugglers extends Enemies {
    private List<Good> goodsReward;
    private int goodsLoss;

    public Smugglers(List<Good> goodsReward, int goodsLoss, int level, int cannonStrength, int flightDays) {
        super(level, cannonStrength, flightDays);
        this.goodsReward = goodsReward;
        this.goodsLoss = goodsLoss;
    }

    public List<Good> getGoodsReward() {
        return goodsReward;
    }

    public int getGoodsLoss() {
        return goodsLoss;
    }

    @Override
    public CardType getCardType() {
        return CardType.SMUGGLERS;
    }

    @Override
    public void apply(PlayerData player){

    }
}
