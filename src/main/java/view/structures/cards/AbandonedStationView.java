package view.structures.cards;

import Model.Good.Good;

import java.util.List;

public class AbandonedStationView extends CardView {
    private int crewRequired;
    private List<Good> goods;
    private int flightDays;

    public AbandonedStationView(int ID, boolean covered, int crewRequired, int flightDays, List<Good> goods) {
        super(ID, covered);
        this.crewRequired = crewRequired;
        this.goods = goods;
        this.flightDays = flightDays;
    }

    @Override
    public void drawCardGui(){

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
                String line = "│  Goods: " + super.printGoodsStation(getGoods());
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

    public List<Good> getGoods() {
        return goods;
    }

    public void setGoods(List<Good> goods) {
        this.goods = goods;
    }
}
