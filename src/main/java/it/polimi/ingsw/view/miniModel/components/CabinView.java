package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewMembers;

public class CabinView extends ComponentView {
    private final String lightBlue = "\033[94m";
    private final String blue = "\033[34m";
    private final String green = "\033[32m";
    private final String yellow = "\033[33m";
    private final String red = "\033[31m";
    private final String reset = "\033[0m";
    private final String color;

    private int crewNumber;
    private CrewMembers crew;

    public CabinView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
        this.crewNumber = 0;
        switch (ID) {
            case 152 -> this.color = blue;
            case 153 -> this.color = green;
            case 154 -> this.color = red;
            case 155 -> this.color = yellow;
            default -> this.color = lightBlue;
        }
    }

    public int getCrewNumber() {
        return crewNumber;
    }

    public void setCrewNumber(int crewNumber) {
        this.crewNumber = crewNumber;
    }

    public void setCrewType(CrewMembers crew) {
        this.crew = crew;
    }

    public CrewMembers getCrewType() {
        return crew;
    }

    public boolean hasPurpleAlien() {
        return crew.equals(CrewMembers.PURPLEALIEN);
    }

    public boolean hasBrownAlien() {
        return crew.equals(CrewMembers.BROWALIEN);
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     */
    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Cabin component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + drawCrew() + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    private String drawCrew() {
        return switch (crewNumber) {
            case 0 -> color + "(" + reset + "   " + color + ")" + reset;
            case 1 -> color + "(" + reset + " " + crew.drawTui() + " " + color + ")" + reset;
            case 2 -> color + "(" + reset + crew.drawTui() + " " + crew.drawTui() + color + ")" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + crewNumber);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.CABIN;
    }

    @Override
    public CabinView clone() {
        CabinView copy = new CabinView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setCrewType(this.crew);
        copy.setCrewNumber(this.crewNumber);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
