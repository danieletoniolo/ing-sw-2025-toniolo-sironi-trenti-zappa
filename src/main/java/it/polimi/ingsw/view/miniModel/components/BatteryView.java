package it.polimi.ingsw.view.miniModel.components;

public class BatteryView extends ComponentView {
    private int numberOfBatteries;
    private int maximumBatteries;
    private String green = "\033[32m";
    private String reset = "\033[0m";

    public BatteryView(int ID, int[] connectors, int maximumBatteries) {
        super(ID, connectors);
        this.maximumBatteries = maximumBatteries;
    }

    public int getMaximumBatteries() {
        return maximumBatteries;
    }

    public void setNumberOfBatteries(int numberOfBatteries) {
        this.numberOfBatteries = numberOfBatteries;
    }

    /**
     * Reduce of 1 the number of batteries
     */
    public void reduceNumberOfButteries() {
        this.numberOfBatteries--;
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     */
    @Override
    public void drawGui() {
        //TODO: Implement the GUI drawing logic for the Battery component here
    }

    @Override
    public String drawLineTui(int line) throws IndexOutOfBoundsException {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + (green + numberOfBatteries + "/" + maximumBatteries + reset) + " " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    @Override
    public TilesTypeView getType() {
        return TilesTypeView.BATTERY;
    }
}
