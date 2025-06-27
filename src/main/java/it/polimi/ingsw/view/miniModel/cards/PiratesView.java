package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.cards.hit.HitView;

import java.util.List;

/**
 * Represents a pirate card view that extends the base CardView.
 * Contains specific attributes for pirate cards including cannon requirements,
 * credits, flight days, and a list of hits with current hit tracking.
 * @author Matteo Zappa
 */
public class PiratesView extends CardView {
    /** The number of cannons required to play this pirate card */
    private final int cannonRequires;
    /** The number of credits this pirate card provides */
    private final int credits;
    /** The number of flight days this pirate card provides */
    private final int flightDays;
    /** The list of hits available on this pirate card */
    private final List<HitView> hits;
    /** The index of the currently selected hit (-1 if none selected) */
    private int currentHit;


    /**
     * Constructs a new PiratesView with the specified attributes.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is face down
     * @param level the level of the card
     * @param cannonRequires the number of cannons required to play this card
     * @param credits the number of credits this card provides
     * @param flightDays the number of flight days this card provides
     * @param hits the list of hits available on this card
     */
    public PiratesView(int ID, boolean covered, int level, int cannonRequires, int credits, int flightDays, List<HitView> hits) {
        super(ID, covered, level);
        this.cannonRequires = cannonRequires;
        this.credits = credits;
        this.flightDays = flightDays;
        this.hits = hits;
        this.currentHit = -1;
    }

    /**
     * Draws a specific line of the text-based user interface representation of this pirate card.
     * If the card is covered, delegates to the parent implementation. Otherwise, renders
     * different lines showing pirate-specific information including strength requirements,
     * hits with current hit indicator, credits, and flight days.
     *
     * @param l the line number to draw (0-9 for a complete card representation)
     * @return a string representing the specified line of the card's TUI display
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│      PIRATES      │";
            case 2 -> Clear;
            case 3 -> "│  StrengthReq: " + getCannonRequires();
            case 4 -> "│  " + (currentHit == 0 ? drawCurrent() : " ") + " Hit1: " + hits.get(0).drawHitTui();
            case 5 -> "│  " + (currentHit == 1 ? drawCurrent() : " ") + " Hit2: " + hits.get(1).drawHitTui();
            case 6 -> "│  " + (currentHit == 2 ? drawCurrent() : " ") + " Hit3: " + hits.get(2).drawHitTui();
            case 7 -> "│  Credit: " + getCredits();
            case 8 -> "│   FlightDays: " + getFlightDays();
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
     * Returns the visual indicator for the currently selected hit.
     *
     * @return a string containing the current hit indicator symbol
     */
    private String drawCurrent(){
        return "◉";
    }

    /**
     * Advances to the next hit in the sequence by incrementing the current hit index.
     */
    public void nextHit() {
        this.currentHit++;
    }

    /**
     * Gets the number of cannons required to play this pirate card.
     *
     * @return the cannon requirement for this card
     */
    public int getCannonRequires() {
        return cannonRequires;
    }

    /**
     * Gets the number of credits this pirate card provides.
     *
     * @return the credit value of this card
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Gets the number of flight days this pirate card provides.
     *
     * @return the flight days value of this card
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the list of hits available on this pirate card.
     *
     * @return an immutable view of the hits list
     */
    public List<HitView> getHits() {
        return hits;
    }

    /**
     * Returns the specific card view type for this pirate card.
     *
     * @return the PIRATES card view type
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.PIRATES;
    }
}
