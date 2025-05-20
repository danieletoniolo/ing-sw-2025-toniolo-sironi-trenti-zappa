package view.miniModel.cards;

import view.miniModel.good.GoodView;

import java.util.List;

public class AbandonedStationView extends CardView {
    private int crewRequired;
    private List<GoodView> goods;
    private int flightDays;

    public AbandonedStationView(int ID, boolean covered, int level, int crewRequired, int flightDays, List<GoodView> goods) {
        super(ID, covered, level);
        this.crewRequired = crewRequired;
        this.goods = goods;
        this.flightDays = flightDays;
    }

    @Override
    public void drawGui(){
        //TODO: Implement GUI drawing logic
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│ ABANDONEDSTATION  │";
            case 2,5,6,7 -> Clear;
            case 3 -> "│  CrewRequired: " + getCrewRequired();
            case 4 -> "│  Goods: " + printGoods();
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

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public int getCrewRequired() {
        return crewRequired;
    }

    public void setCrewRequired(int crewNeeded) {
        this.crewRequired = crewNeeded;
    }

    public List<GoodView> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodView> goods) {
        this.goods = goods;
    }

    private String printGoods() {
        StringBuilder goodsString = new StringBuilder();
        for (GoodView good : goods) {
            goodsString.append(good.drawTui()).append(" ");
        }
        return goodsString.toString();
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.ABANDONEDSTATION;
    }
}
