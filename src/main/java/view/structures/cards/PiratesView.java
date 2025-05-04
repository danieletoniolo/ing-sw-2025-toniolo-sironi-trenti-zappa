package view.structures.cards;

import Model.Cards.Hits.Direction;
import Model.Cards.Hits.HitType;
import org.javatuples.Pair;

import java.util.List;

public class PiratesView extends CardView {
    private int cannonRequires;
    private int credits;
    private int flightDays;
    private List<Pair<HitType, Direction>> hits;

    @Override
    public void drawCardGui() {

    }
}
