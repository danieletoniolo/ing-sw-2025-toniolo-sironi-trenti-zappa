package view.structures.cards;

public class AbandonedStationView extends CardView {
    private int crewNeeded;
    private String[] goods;
    private int flightDays;

    @Override
    public void drawCardGui(){

    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public int getCrewNeeded() {
        return crewNeeded;
    }

    public void setCrewNeeded(int crewNeeded) {
        this.crewNeeded = crewNeeded;
    }

    public String[] getGoods() {
        return goods;
    }

    public void setGoods(String[] goods) {
        this.goods = goods;
    }
}
