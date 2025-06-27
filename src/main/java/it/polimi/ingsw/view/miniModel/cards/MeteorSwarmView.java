package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.cards.hit.HitView;

import java.util.List;

/**
 * Represents a view for the Meteor Swarm card in the mini model.
 * This class extends CardView and manages the display of multiple hits
 * with a current hit indicator for the text-based user interface.
 */
public class MeteorSwarmView extends CardView {
    /** List of hits associated with this meteor swarm card */
    public List<HitView> hits;
    /** Index of the currently selected hit (0-based) */
    private int currentHit;

    /**
     * Constructs a new MeteorSwarmView with the specified parameters.
     *
     * @param ID the unique identifier for this card
     * @param covered whether the card is currently covered/hidden
     * @param level the level of the card
     * @param hits the list of hits associated with this meteor swarm
     */
    public MeteorSwarmView(int ID, boolean covered, int level, List<HitView> hits) {
        super(ID, covered, level);
        this.hits = hits;
        this.currentHit = 0;
    }

    /**
     * Draws a specific line of the card for the text-based user interface.
     *
     * @param l the line number to draw (0-based)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);


        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│   METEOR SWARM    │";
            case 2,8 -> Clear;
            case 3 -> (!hits.isEmpty() ?  ("│   " + (currentHit == 0 ? drawCurrent() : " ") + " Hit1: " + hits.getFirst().drawHitTui()) : Clear);
            case 4 -> (hits.size() >= 2 ? ("│   " + (currentHit == 1 ? drawCurrent() : " ") + " Hit2: " + hits.get(1).drawHitTui()) : Clear);
            case 5 -> (hits.size() >= 3 ? ("│   " + (currentHit == 2 ? drawCurrent() : " ") + " Hit3: " + hits.get(2).drawHitTui()) : Clear);
            case 6 -> (hits.size() >= 4 ? ("│   " + (currentHit == 3 ? drawCurrent() : " ") + " Hit4: " + hits.get(3).drawHitTui()) : Clear);
            case 7 -> (hits.size() >= 5 ? ("│   " + (currentHit == 4 ? drawCurrent() : " ") + " Hit5: " + hits.get(4).drawHitTui()) : Clear);
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

    /**
     * Gets the list of hits associated with this meteor swarm card.
     *
     * @return the list of HitView objects representing the hits
     */
    public List<HitView> getHits() {
        return hits;
    }

    /**
     * Advances to the next hit in the sequence.
     * Increments the currentHit index to move the selection indicator
     * to the next hit in the list.
     */
    public void nextHit() {
        this.currentHit++;
    }

    /**
     * Draws the current hit indicator symbol for the text-based user interface.
     *
     * @return the Unicode symbol "◉" used to indicate the currently selected hit
     */
    private String drawCurrent(){
        return "◉";
    }

    /**
     * Gets the card view type for this meteor swarm card.
     *
     * @return the CardViewType enum value METEORSSWARM
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.METEORSSWARM;
    }
}
