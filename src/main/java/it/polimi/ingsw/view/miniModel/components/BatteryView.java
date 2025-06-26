package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;

public class BatteryView extends ComponentView {
    private int numberOfBatteries;
    private final int maximumBatteries;
    private static final String green = "\033[32m";
    private static final String reset = "\033[0m";
    private Pair<Node, ComponentController> batteryPair;

    public BatteryView(int ID, int[] connectors, int clockWise, int maximumBatteries) {
        super(ID, connectors, clockWise);
        this.maximumBatteries = maximumBatteries;
    }

    public int getMaximumBatteries() {
        return maximumBatteries;
    }

    public void setNumberOfBatteries(int numberOfBatteries) {
        this.numberOfBatteries = numberOfBatteries;
        notifyObservers();
    }

    public int getNumberOfBatteries() {
        return numberOfBatteries;
    }

    @Override
    public Pair<Node, ComponentController> getNode() {
        try {
            if (batteryPair != null) return batteryPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/battery.fxml"));
            Parent root = loader.load();

            BatteryController controller = loader.getController();
            controller.setModel(this);

            batteryPair = new Pair<>(root, controller);
            return batteryPair;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

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
    public ComponentTypeView getType() {
        return ComponentTypeView.BATTERY;
    }

    @Override
    public BatteryView clone() {
        BatteryView copy = new BatteryView(this.getID(), this.getConnectors(), this.getClockWise(), this.getMaximumBatteries());
        copy.setNumberOfBatteries(this.numberOfBatteries);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
