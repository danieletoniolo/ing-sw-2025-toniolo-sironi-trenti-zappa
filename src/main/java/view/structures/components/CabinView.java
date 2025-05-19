package view.structures.components;

public class CabinView extends ComponentView {
    private final String brown = "\033[38;5;220m";
    private final String purple = "\033[35m";
    private final String lightBlue = "\033[94m";
    private final String blue = "\033[34m";
    private final String green = "\033[32m";
    private final String yellow = "\033[33m";
    private final String red = "\033[31m";
    private final String reset = "\033[0m";
    private String color;

    private int crewNumber;
    private boolean purpleAlien;
    private boolean brownAlien;
    private boolean centralCabin;

    public CabinView(int ID, int[] connectors) {
        super(ID, connectors);
        this.crewNumber = 0;
        this.purpleAlien = false;
        this.brownAlien = false;
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

    public boolean hasPurpleAlien() {
        return purpleAlien;
    }

    public void setPurpleAlien(boolean purpleAlien) {
        this.purpleAlien = purpleAlien;
    }

    public boolean hasBrownAlien() {
        return brownAlien;
    }

    public void setBrownAlien(boolean brownAlien) {
        this.brownAlien = brownAlien;
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
            case 1 -> color + "(" + reset + " " + drawSingle() + " " + color + ")" + reset;
            case 2 -> color + "(" + reset + " " + drawSingle() + " " + drawSingle() + color + ")" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + crewNumber);
        };
    }

    private String drawSingle() {
        if (purpleAlien) return purple + "A" + reset;
        if (brownAlien) return brown + "A" + reset;
        return "C";
    }
}
