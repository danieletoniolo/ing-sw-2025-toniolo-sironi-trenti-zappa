package view.structures.cards;

public class AbandonedShipView extends CardView {
    private int crewLoss;
    private int credit;
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

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getCrewLoss() {
        return crewLoss;
    }

    public void setCrewLoss(int crewLoss) {
        this.crewLoss = crewLoss;
    }
}
