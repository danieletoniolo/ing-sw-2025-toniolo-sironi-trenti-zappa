package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public class BatteryView extends ComponentView {
    private int numberOfBatteries;
    private final int maximumBatteries;
    private static final String green = "\033[32m";
    private static final String reset = "\033[0m";

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

    @Override
    public Node createGuiNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/battery.fxml"));
            Parent root = loader.load();

            BatteryController controller = loader.getController();
            controller.setModel(this);

            return root;
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
