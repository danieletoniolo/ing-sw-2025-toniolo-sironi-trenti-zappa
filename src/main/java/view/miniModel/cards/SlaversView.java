package view.miniModel.cards;

public class SlaversView extends CardView {
    private int cannonRequired;
    private int credits;
    private int flightDays;
    private int crewLoss;

    public SlaversView(int ID, boolean covered, int level, int cannonRequired, int credits, int flightDays, int crewLoss) {
        super(ID, covered, level);
        this.cannonRequired = cannonRequired;
        this.credits = credits;
        this.flightDays = flightDays;
        this.crewLoss = crewLoss;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch (l) {
            case 0 -> Up;
            case 1 -> "│      SLAVERS      │";
            case 2, 6, 7 -> Clear;
            case 3 -> "│   StrenghtReq: " + getCannonRequired();
            case 4 -> "│   CrewLost: " + getCrewLoss();
            case 5 -> "│   Credit: " + getCredits();
            case 8 -> "│   FlightDays: " + getFlightDays();
            case 9 -> Down;
            default -> null;
        });

        while (line.length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.length() == getColsToDraw() - 1) {
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

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.SLAVERS;
    }
}
