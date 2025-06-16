package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.miniModel.MiniModelListener;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class BatteryView extends ComponentView {
    private final List<MiniModelListener> listeners = new ArrayList<>();
    private int numberOfBatteries;
    private final int maximumBatteries;
    private final String green = "\033[32m";
    private final String reset = "\033[0m";

    public BatteryView(int ID, int[] connectors, int clockWise, int maximumBatteries) {
        super(ID, connectors, clockWise);
        this.maximumBatteries = maximumBatteries;
    }

    public int getMaximumBatteries() {
        return maximumBatteries;
    }

    public void setNumberOfBatteries(int numberOfBatteries) {
        this.numberOfBatteries = numberOfBatteries;
    }

    public int getNumberOfBatteries() {
        return numberOfBatteries;
    }

    public void addListener(MiniModelListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MiniModelListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (MiniModelListener listener : listeners) {
            listener.onModelChanged();
        }
    }

    /**
     * Draws the component GUI.
     * This method is called to draw the component GUI.
     *
     * @return an Image representing the image of the component
     */
    @Override
    public Image drawGui() {
        String path = "/image/tiles/" + this.getID() + ".jpg";
        Image img = new Image(getClass().getResource(path).toExternalForm());
        return img;
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

    @Override
    public BatteryView clone() {
        BatteryView copy = new BatteryView(this.getID(), this.getConnectors(), this.getClockWise(), this.getMaximumBatteries());
        copy.setNumberOfBatteries(this.numberOfBatteries);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
