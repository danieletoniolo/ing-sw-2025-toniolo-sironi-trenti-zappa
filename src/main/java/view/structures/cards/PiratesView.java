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

    public List<Pair<HitType, Direction>> getHits() {
        return hits;
    }

    public void setHits(List<Pair<HitType, Direction>> hits) {
        this.hits = hits;
    }
}
