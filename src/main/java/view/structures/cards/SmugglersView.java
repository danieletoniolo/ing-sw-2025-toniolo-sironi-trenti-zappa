package view.structures.cards;

import view.structures.good.GoodView;

import java.util.List;

public class SmugglersView extends CardView{
    private int cannonRequired;
    private int goodsLoss;
    private int flightDays;
    private List<GoodView> goods;

    public SmugglersView(int ID, boolean covered, int level, int cannonRequired, int goodsLoss, int flightDays, List<GoodView> goods) {
        super(ID, covered, level);
        this.cannonRequired = cannonRequired;
        this.goodsLoss = goodsLoss;
        this.flightDays = flightDays;
        this.goods = goods;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│     SMUGGLERS     │";
            case 2,6,7 -> Clear;
            case 3 -> "│   StrengthReq: " + getCannonRequired();
            case 4 -> "│   GoodLost: " + getGoodsLoss();
            case 5 -> "│   Good: " + printGoods();
            case 8 -> "│   FlightDays: " + getFlightDays();
            case 9 -> Down;
            default -> null;
        });

        while (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.toString().replaceAll("\033\\[[0-9;]*m", "").length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
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

    public List<GoodView> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodView> goods) {
        this.goods = goods;
    }

    private String printGoods() {
        StringBuilder sb = new StringBuilder();
        for (GoodView good : goods) {
            sb.append(good.drawTui()).append(" ");
        }
        return sb.toString();
    }
}
