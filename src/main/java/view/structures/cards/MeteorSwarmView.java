package view.structures.cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.HitType;
import org.javatuples.Pair;

import java.util.List;

public class MeteorSwarmView extends CardView {
    public List<Pair<HitType, Direction>> hits;

    @Override
    public void drawCardGui(){

    }

    public List<Pair<HitType, Direction>> getHits() {
        return hits;
    }

    public void setHits(List<Pair<HitType, Direction>> hits) {
        this.hits = hits;
    }
}
