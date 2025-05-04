package view.structures.cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.HitType;
import org.javatuples.Pair;

import java.util.List;

public class CombactZoneView extends CardView {
    private int crewLoss;
    private int flightDaysLoss;
    private List<Pair<HitType, Direction>> hits;

    //TODO: Da notare il livello della carta

    @Override
    public void drawCardGui() {

    }
}
