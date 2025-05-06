package view.structures.cards;

import Model.Good.Good;

import java.util.List;

public class SmugglersView extends CardView{
    private int cannonRequired;
    private int goodsLoss;
    private int flightDays;
    private List<Good> goods;

    public SmugglersView(int ID, boolean covered, int cannonRequired, int goodsLoss, int flightDays, List<Good> goods) {
        super(ID, covered);
        this.cannonRequired = cannonRequired;
        this.goodsLoss = goodsLoss;
        this.flightDays = flightDays;
        this.goods = goods;
    }

    @Override
    public void drawCardGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│      SMUGGLERS      │";
            case 2,6,7 -> Clear;
            case 3 -> {
                String line = "│  StrenghtReq: " + getCannonRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  GoodLost: " + getGoodsLoss();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "│  Good: " + printGoodsStation(getGoods());
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 8 -> {
                String line = "│  FlightDays: " + getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 9 -> Down;
            default -> null;
        };
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

    public List<Good> getGoods() {
        return goods;
    }

    public void setGoods(List<Good> goods) {
        this.goods = goods;
    }
}
