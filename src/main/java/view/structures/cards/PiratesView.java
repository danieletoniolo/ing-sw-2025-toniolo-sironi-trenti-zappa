package view.structures.cards;

import Model.Cards.Hits.Hit;

import java.util.List;

public class PiratesView extends CardView {
    private int cannonRequires;
    private int credits;
    private int flightDays;
    private List<Hit> hits;

    public PiratesView(int ID, boolean covered, int cannonRequires, int credits, int flightDays, List<Hit> hits) {
        super(ID, covered);
        this.cannonRequires = cannonRequires;
        this.credits = credits;
        this.flightDays = flightDays;
        this.hits = hits;
    }

    @Override
    public void drawCardGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        return switch(l) {
            case 0 -> Up;
            case 1 -> "│       PIRATES       │";
            case 2 -> Clear;
            case 3 -> {
                String line = "│  StrenghtReq: " + getCannonRequires();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│  Hit1: " + printHit(getHits(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "│  Hit2: " + printHit(getHits(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 6 -> {
                String line = "│  Hit3: " + printHit(getHits(), 2);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 7 -> {
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

    public int getCannonRequires() {
        return cannonRequires;
    }

    public void setCannonRequires(int cannonRequires) {
        this.cannonRequires = cannonRequires;
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

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }
}
