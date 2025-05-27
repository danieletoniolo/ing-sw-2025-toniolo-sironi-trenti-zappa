package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.cards.hit.HitView;

import java.util.List;

public class MeteorSwarmView extends CardView {
    public List<HitView> hits;

    public MeteorSwarmView(int ID, boolean covered, int level, List<HitView> hits) {
        super(ID, covered, level);
        this.hits = hits;
    }

    @Override
    public void drawGui(){

    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);


        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│    METEORSWARM    │";
            case 2,8 -> Clear;
            case 3 -> (hits.isEmpty() ?   ("│  Hit1: " + hits.getFirst().drawHitTui()) : Clear);
            case 4 -> (hits.size() >= 2 ? ("│  Hit2: " + hits.get(1).drawHitTui()) : Clear);
            case 5 -> (hits.size() >= 3 ? ("│  Hit2: " + hits.get(2).drawHitTui()) : Clear);
            case 6 -> (hits.size() >= 4 ? ("│  Hit2: " + hits.get(3).drawHitTui()) : Clear);
            case 7 -> (hits.size() >= 5 ? ("│  Hit2: " + hits.get(4).drawHitTui()) : Clear);
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

    public List<HitView> getHits() {
        return hits;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.METEORSSWARM;
    }
}
