package view.structures.cards;

import view.structures.cards.hit.HitView;

import java.util.List;

public class PiratesView extends CardView {
    private int cannonRequires;
    private int credits;
    private int flightDays;
    private List<HitView> hits;

    public PiratesView(int ID, boolean covered, int level, int cannonRequires, int credits, int flightDays, List<HitView> hits) {
        super(ID, covered, level);
        this.cannonRequires = cannonRequires;
        this.credits = credits;
        this.flightDays = flightDays;
        this.hits = hits;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│      PIRATES      │";
            case 2 -> Clear;
            case 3 -> "│  StrenghtReq: " + getCannonRequires();
            case 4 -> "│  Hit1: " + hits.get(0).drawHitTui();
            case 5 -> "│  Hit2: " + hits.get(1).drawHitTui();
            case 6 -> "│  Hit3: " + hits.get(2).drawHitTui();
            case 7 -> "│  Credit: " + getCredits();
            case 8 -> "│  FlightDays: " + getFlightDays();
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

    public List<HitView> getHits() {
        return hits;
    }

    public void setHits(List<HitView> hits) {
        this.hits = hits;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.PIRATES;
    }
}
