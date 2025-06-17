package it.polimi.ingsw.view.miniModel.cards;

import javafx.scene.image.Image;

public class AbandonedShipView extends CardView {
    private final int crewLoss;
    private final int credit;
    private final int flightDays;

    public AbandonedShipView(int ID, boolean covered, int level, int crewLoss, int credit, int flightDays) {
        super(ID, covered, level);
        this.crewLoss = crewLoss;
        this.credit = credit;
        this.flightDays = flightDays;
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

    public int getFlightDays() {
        return flightDays;
    }

    public int getCredit() {
        return credit;
    }

    public int getCrewLoss() {
        return crewLoss;
    }

    @Override
    public CardViewType getCardViewType() {
        return CardViewType.ABANDONEDSHIP;
    }
}
