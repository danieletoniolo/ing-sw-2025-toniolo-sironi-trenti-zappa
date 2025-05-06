package view.structures.cards;

public class SlaversView extends CardView {
    private int cannonRequired;
    private int credits;
    private int flightDays;
    private int crewLoss;

    @Override
    public void drawCardGui() {

    }

    public int getCannonRequired() {
        return cannonRequired;
    }

    public void setCannonRequired(int cannonRequired) {
        this.cannonRequired = cannonRequired;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDays) {
        this.flightDays = flightDays;
    }

    public int getCrewLoss() {
        return crewLoss;
    }

    public void setCrewLoss(int crewLoss) {
        this.crewLoss = crewLoss;
    }
}
