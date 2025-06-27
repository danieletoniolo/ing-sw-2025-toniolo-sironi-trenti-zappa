package it.polimi.ingsw.view.miniModel.cards;

/**
 * View representation of a Slavers card in the game.
 * This class extends CardView and contains specific attributes for slavers cards
 * including cannon requirements, credits, flight days, and crew loss.
 * @author Matteo Zappa
 */
public class SlaversView extends CardView {
    /** The number of cannons required to use this slavers card */
    private final int cannonRequired;
    /** The number of credits gained from this slavers card */
    private final int credits;
    /** The number of flight days associated with this slavers card */
    private final int flightDays;
    /** The number of crew members lost when using this slavers card */
    private final int crewLoss;

    /**
     * Constructs a new SlaversView with the specified parameters.
     *
     * @param ID the unique identifier of the card
     * @param covered whether the card is covered/face down
     * @param level the level of the card
     * @param cannonRequired the number of cannons required to use this card
     * @param credits the number of credits gained from this card
     * @param flightDays the number of flight days associated with this card
     * @param crewLoss the number of crew members lost when using this card
     */
    public SlaversView(int ID, boolean covered, int level, int cannonRequired, int credits, int flightDays, int crewLoss) {
        super(ID, covered, level);
        this.cannonRequired = cannonRequired;
        this.credits = credits;
        this.flightDays = flightDays;
        this.crewLoss = crewLoss;
    }

    /**
     * Draws a specific line of the text-based user interface representation of this slavers card.
     *
     * @param l the line number to draw (0-based indexing)
     * @return a string representation of the specified line
     */
    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch (l) {
            case 0 -> Up;
            case 1 -> "│      SLAVERS      │";
            case 2, 6, 7 -> Clear;
            case 3 -> "│   StrengthReq: " + getCannonRequired();
            case 4 -> "│   CrewLost: " + getCrewLoss();
            case 5 -> "│   Credit: " + getCredits();
            case 8 -> "│   FlightDays: " + getFlightDays();
            case 9 -> Down;
            default -> null;
        });

        while (line.length() < getColsToDraw() - 1) {
            line.append(" ");
        }
        if (line.length() == getColsToDraw() - 1) {
            line.append("│");
        }
        return line.toString();
    }

    /**
     * Gets the number of cannons required to use this slavers card.
     *
     * @return the cannon requirement
     */
    public int getCannonRequired() {
        return cannonRequired;
    }

    /**
     * Gets the number of credits gained from this slavers card.
     *
     * @return the credits value
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Gets the number of flight days associated with this slavers card.
     *
     * @return the flight days
     */
    public int getFlightDays() {
        return flightDays;
    }

    /**
     * Gets the number of crew members lost when using this slavers card.
     *
     * @return the crew loss
     */
    public int getCrewLoss() {
        return crewLoss;
    }

    /**
     * Gets the type of this card view.
     *
     * @return the card view type (SLAVERS)
     */
    @Override
    public CardViewType getCardViewType() {
        return CardViewType.SLAVERS;
    }
}
