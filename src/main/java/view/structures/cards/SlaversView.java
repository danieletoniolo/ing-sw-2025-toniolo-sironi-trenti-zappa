package view.structures.cards;

public class SlaversView extends CardView {
    private int cannonRequired;
    private int credits;
    private int flightDays;
    private int crewLoss;

    public SlaversView(int ID, boolean covered, int cannonRequired, int credits, int flightDays, int crewLoss) {
        super(ID, covered);
        this.cannonRequired = cannonRequired;
        this.credits = credits;
        this.flightDays = flightDays;
        this.crewLoss = crewLoss;
    }

    @Override
    public void drawCardGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│       SLAVERS       │";
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
                String line = "│  CrewLost: " + getCrewLoss();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "│  Credit: " + getCredits();
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
