package view.miniModel.cards;

import view.miniModel.cards.hit.HitView;

import java.util.List;

public class CombatZoneView extends CardView {
    private int loss;
    private int flightDays;
    private List<HitView> hits;

    public CombatZoneView(int ID, boolean covered, int level, int loss, int flightDays, List<HitView> hits) {
        super(ID, covered, level);
        this.loss = loss;
        this.flightDays = flightDays;
        this.hits = hits;
    }

    @Override
    public void drawGui() {

    }

    @Override
    public String drawLineTui(int l) {
        if(isCovered()) return super.drawLineTui(l);


        StringBuilder line = new StringBuilder(switch (l) {
            case 0 -> Up;
            case 1 -> "│     COMBATZONE    │";
            case 2 -> Clear;
            case 3 -> (getID() == 15 ? "│  Cr " : "│  Ca ") + "=> FDays: " + getFlightDays();
            case 4 -> "│  En => GoodL: " + getLoss();
            case 5 -> (getID() == 15 ? "│  Ca " : "│  Cr ") + "=> H1: " + hits.get(0).drawHitTui();
            case 6 -> "│        H2: " + hits.get(1).drawHitTui();
            case 7 -> getHits().size() < 3 ? Clear : "│        H3: " + hits.get(2).drawHitTui();
            case 8 -> getHits().size() < 4 ? Clear : "│        H4: " + hits.get(3).drawHitTui();
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

    public int getLoss() {
        return loss;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public List<HitView> getHits() {
        return hits;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.COMBATZONE;
    }
}
