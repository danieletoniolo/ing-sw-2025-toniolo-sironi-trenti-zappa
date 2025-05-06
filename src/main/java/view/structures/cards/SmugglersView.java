package view.structures.cards;

public class SmugglersView extends CardView{
    private int cannonRequired;
    private int goodsLoss;
    private int flightDays;
    private String[] goods;

    @Override
    public void drawCardGui() {

    }

    public int getCannonRequired() {
        return cannonRequired;
    }

    public void setCannonRequired(int cannonRequired) {
        this.cannonRequired = cannonRequired;
    }

    public int getGoodsLoss() {
        return goodsLoss;
    }

    public void setGoodsLoss(int goodsLoss) {
        this.goodsLoss = goodsLoss;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public String[] getGoods() {
        return goods;
    }

    public void setGoods(String[] goods) {
        this.goods = goods;
    }
}
