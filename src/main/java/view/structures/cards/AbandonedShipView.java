package view.structures.cards;

public class AbandonedShipView extends CardView {
    private int crewLoss;
    private int credit;
    private int flightDays;

    public AbandonedShipView(int ID, boolean covered, int level, int crewLoss, int credit, int flightDays) {
        super(ID, covered, level);
        this.crewLoss = crewLoss;
        this.credit = credit;
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
            case 1 -> "│    ABANDONEDSHIP    │";
            case 2,5,6,7 -> Clear;
            case 3 -> {
                String line = "│  CrewLost: " + getCrewLoss();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  Credit: " + getCredit();
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
