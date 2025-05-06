package view.structures.cards;

import Model.Cards.Hits.Hit;

import java.util.List;

public class CombatZoneView extends CardView {
    private int loss;
    private int flightDays;
    private List<Hit> hits;

    public CombatZoneView(int ID, boolean covered, int loss, int flightDays, List<Hit> hits) {
        super(ID, covered);
        this.loss = loss;
        this.flightDays = flightDays;
        this.hits = hits;
    }

    @Override
    public void drawCardGui() {

    }

    @Override
    public String drawLineTui(int l) {
        if(isCovered()) return super.drawLineTui(l);

        return switch (l) {
            case 0 -> Up;
            case 1 -> "│      COMBATZONE     │";
            case 2 -> Clear;
            case 3 -> {
                String line = "";
                if(getID() == 15){
                    line = "│ Cr ";
                } else {
                    line = "│ Ca ";
                }
                line += "=> FDays: " + getFlightDays();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 4 -> {
                String line = "│ En => GoodL: " + getLoss();
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 5 -> {
                String line = "";
                if(getID() == 15){
                    line += "│ Ca ";
                } else {
                    line += "│ Cr ";
                }
                line += "=> H1: " + printHit(getHits(), 0);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 6 -> {
                String line = "│       H2: " + printHit(getHits(), 1);
                while (line.length() < 22) {
                    line += " ";
                }
                line += "│";
                yield line;
            }
            case 7 -> {
                if (getHits().size() < 3) {
                    yield Clear;
                } else {
                    String line = "│       H3: " + printHit(getHits(), 2);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 8 -> {
                if (getHits().size() < 4) {
                    yield Clear;
                } else {
                    String line = "│       H4: " + printHit(getHits(), 3);
                    while (line.length() < 22) {
                        line += " ";
                    }
                    line += "│";
                    yield line;
                }
            }
            case 9 -> Down;
            default -> null;
        };
    }

    public int getLoss() {
        return loss;
    }

    public void setLoss(int Loss) {
        this.loss = Loss;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public void setFlightDays(int flightDaysLoss) {
        this.flightDays = flightDaysLoss;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }
}
