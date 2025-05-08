package view.structures.cards;

import view.structures.good.GoodView;

import java.util.List;

public class AbandonedStationView extends CardView {
    private int crewRequired;
    private List<GoodView> goods;
    private int flightDays;

    public AbandonedStationView(int ID, boolean covered, int crewRequired, int flightDays, List<GoodView> goods) {
        super(ID, covered);
        this.crewRequired = crewRequired;
        this.goods = goods;
        this.flightDays = flightDays;
    }

    @Override
    public void drawCardGui(){
        //TODO: Implement GUI drawing logic
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│  ABANDONEDSTATION   │";
            case 2,5,6,7 -> Clear;
            case 3 -> {
                String line = "│  CrewRequired: " + getCrewRequired();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  Goods: " + printGoods();
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
            goodsString.append(good.drawTui());
        }
        return goodsString.toString().trim();
    }
}
