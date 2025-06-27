package it.polimi.ingsw.view.miniModel.cards;

/**
 * Represents the view of an Abandoned Ship card in the game.
 * This card type causes crew loss but provides credits and flight days as compensation.
 * @author Matteo Zappa
 */
public class AbandonedShipView extends CardView {
    /** The number of crew members lost when this card is encountered */
    private final int crewLoss;
    /** The amount of credits gained from this card */
    private final int credit;
    /** The number of flight days added by this card */
    private final int flightDays;

    /**
     * Constructs a new AbandonedShipView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is face down or revealed
     * @param level the level of the card
     * @param crewLoss the number of crew members lost
     * @param credit the amount of credits gained
     * @param flightDays the number of flight days added
     */
    public AbandonedShipView(int ID, boolean covered, int level, int crewLoss, int credit, int flightDays) {
        super(ID, covered, level);
        this.crewLoss = crewLoss;
        this.credit = credit;
        this.flightDays = flightDays;
    }

    /**
     * Draws a specific line of the card's TUI representation.
     * If the card is covered, delegates to the parent class implementation.
     * Otherwise, renders the abandoned ship card with crew loss, credit, and flight days information.
     *
     * @param l the line number to draw (0-based indexing)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch(l) {
            case 0 -> Up;
            case 1 -> "│   ABANDONEDSHIP   │";
            case 2,5,6,7 -> Clear;
            case 3 -> "│   CrewLost: " + getCrewLoss();
            case 4 -> "│   Credit: " + getCredit();
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
     * Gets the number of flight days added by this abandoned ship card.
     *
     * @return the number of flight days
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the amount of credits gained from this abandoned ship card.
     *
     * @return the amount of credits
     */
    public int getCredit() {
        return credit;
    }

    /**
     * Gets the number of crew members lost when this abandoned ship card is encountered.
     *
     * @return the number of crew members lost
     */
    public int getCrewLoss() {
        return crewLoss;
    }

    /**
     * Gets the specific type of this card view.
     *
     * @return the card view type for abandoned ship cards
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.ABANDONEDSHIP;
    }
}
