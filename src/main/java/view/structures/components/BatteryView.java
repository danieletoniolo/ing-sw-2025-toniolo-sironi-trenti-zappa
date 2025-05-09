package view.structures.components;

public class BatteryView extends ComponentView {
    private int numberOfBatteries;
    private int maximumBatteries;

    public BatteryView(int ID, int[] connectors, int maximumBatteries) {
        super(ID, connectors);
        this.maximumBatteries = maximumBatteries;
    }

    public int getNumberOfBatteries() {
        return numberOfBatteries;
    }

    public void setNumberOfBatteries(int numberOfBatteries) {
        this.numberOfBatteries = numberOfBatteries;
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     */
    @Override
    public void drawComponentGui() {
        //TODO: Implement the GUI drawing logic for the Battery component here
    }

    @Override
    public String drawLineTui(int line) throws IndexOutOfBoundsException {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 4, 3 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + "  Battery  " + super.drawRight(line);
            case 2 -> super.drawLeft(line) + "    " + getNumberOfBatteries() + "/" + maximumBatteries + "    " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }
}
