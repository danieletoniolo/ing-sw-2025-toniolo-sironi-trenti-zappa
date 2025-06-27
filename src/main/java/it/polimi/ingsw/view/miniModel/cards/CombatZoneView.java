package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.miniModel.cards.hit.HitView;

import java.util.List;

/**
 * Represents a Combat Zone card in the view layer of the MiniModel.
 * This card type contains information about combat losses, flight days, and hit effects.
 * @author Matteo Zappa
 */
public class CombatZoneView extends CardView {
    /** The loss value associated with this combat zone */
    private final int loss;
    /** The number of flight days for this combat zone */
    private final int flightDays;
    /** List of hit effects available on this combat zone card */
    private final List<HitView> hits;
    /** Counter for tracking combat zone state */
    private int cont;

    /**
     * Constructs a new CombatZoneView with the specified parameters.
     *
     * @param ID the unique identifier for this card
     * @param covered whether the card is face down or face up
     * @param level the level of this card
     * @param loss the loss value associated with this combat zone
     * @param flightDays the number of flight days for this combat zone
     * @param hits the list of hit effects available on this card
     */
    public CombatZoneView(int ID, boolean covered, int level, int loss, int flightDays, List<HitView> hits) {
        super(ID, covered, level);
        this.loss = loss;
        this.flightDays = flightDays;
        this.hits = hits;
        this.cont = 0;
    }

    /**
     * Draws a specific line of the Text User Interface representation for this combat zone card.
     * If the card is covered, delegates to the parent class implementation.
     * Otherwise, renders different parts of the card based on the line number including
     * combat zone type, flight days, loss values, and hit effects.
     *
     * @param l the line number to draw (0-9 for a complete card representation)
     * @return a formatted string representing the specified line of the card's TUI display
     */
    @Override
    public String drawLineTui(int l) {
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch (l) {
            case 0 -> Up;
            case 1 -> "│     COMBATZONE    │";
            case 2 -> Clear;
            case 3 -> (getID() == 15 ? "│  Cr " : "│  Ca ") + "=> FDays: " + getFlightDays();
            case 4 -> "│  En => " + (getLevel() == 2 ? "GoodL: " : "CrewL: ") + getLoss();
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

    /**
     * Sets the counter value for tracking combat zone state.
     *
     * @param cont the new counter value
     */
    public void setCont(int cont) {
        this.cont = cont;
    }

    /**
     * Gets the current counter value for tracking combat zone state.
     *
     * @return the current counter value
     */
    public int getCont() {
        return cont;
    }

    /**
     * Gets the loss value associated with this combat zone.
     *
     * @return the loss value
     */
    public int getLoss() {
        return loss;
    }

    /**
     * Gets the number of flight days for this combat zone.
     *
     * @return the number of flight days
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the list of hit effects available on this combat zone card.
     *
     * @return the list of hit effects
     */
    public List<HitView> getHits() {
        return hits;
    }

    /**
     * Gets the card view type for this combat zone card.
     *
     * @return the card view type (COMBATZONE)
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.COMBATZONE;
    }
}
