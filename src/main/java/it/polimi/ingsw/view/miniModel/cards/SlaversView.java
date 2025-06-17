package it.polimi.ingsw.view.miniModel.cards;

import javafx.scene.image.Image;

public class SlaversView extends CardView {
    private int cannonRequired;
    private int credits;
    private int flightDays;
    private int crewLoss;

    public SlaversView(int ID, boolean covered, int level, int cannonRequired, int credits, int flightDays, int crewLoss) {
        super(ID, covered, level);
        this.cannonRequired = cannonRequired;
        this.credits = credits;
        this.flightDays = flightDays;
        this.crewLoss = crewLoss;
    }

    /**
     * Draws the card GUI.
     * This method is called to draw the card GUI.
     *
     * @return an Image representing the image of the card
     */
    @Override
    public Image drawGui() {
        String path = "/image/card/" + this.getID() + ".jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
    }

    @Override
    public String drawLineTui(int l){
        if(isCovered()) return super.drawLineTui(l);

        StringBuilder line = new StringBuilder(switch (l) {
            case 0 -> Up;
            case 1 -> "│      SLAVERS      │";
            case 2, 6, 7 -> Clear;
            case 3 -> "│   StrenghtReq: " + getCannonRequired();
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

    public int getCannonRequired() {
        return cannonRequired;
    }

    public int getCredits() {
        return credits;
    }

    public int getFlightDays() {
        return flightDays;
    }

    public int getCrewLoss() {
        return crewLoss;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.SLAVERS;
    }
}
