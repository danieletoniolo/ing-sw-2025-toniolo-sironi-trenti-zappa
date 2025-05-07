package view.structures.components;

public class CabinView extends ComponentView {
    private int crewNumber;
    private boolean purpleAlien;
    private boolean brownAlien;

    public CabinView(int ID, int[] connectors) {
        super(ID, connectors);
        this.crewNumber = 0;
        this.purpleAlien = false;
        this.brownAlien = false;
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
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Cabin component here
    }

    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "   Cabin   " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "  Crew: " + getCrewNumber() + "  " + super.drawRight(line);
            case 3 -> super.drawLeft(line) + "  Alien:" + (hasPurpleAlien() ? "P  " : hasBrownAlien() ? "B  " : "X  ") + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }
}
