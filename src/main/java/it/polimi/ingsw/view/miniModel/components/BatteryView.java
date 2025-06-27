package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;

/**
 * Represents the view component for a battery in the application.
 * This class extends ComponentView and handles the visual representation
 * and state management of battery components.
 */
public class BatteryView extends ComponentView {
    /** Current number of batteries */
    private int numberOfBatteries;
    /** Maximum number of batteries this component can hold */
    private final int maximumBatteries;
    /** ANSI color code for green text in terminal output */
    private static final String green = "\033[32m";
    /** ANSI reset code to clear terminal formatting */
    private static final String reset = "\033[0m";
    /** Cached JavaFX node and controller pair for GUI representation */
    private Pair<Node, ComponentController> batteryPair;

    /**
     * Constructs a new BatteryView with the specified parameters.
     *
     * @param ID the unique identifier for this component
     * @param connectors array of connector positions
     * @param clockWise the clockwise orientation value
     * @param maximumBatteries the maximum number of batteries this component can hold
     */
    public BatteryView(int ID, int[] connectors, int clockWise, int maximumBatteries) {
        super(ID, connectors, clockWise);
        this.maximumBatteries = maximumBatteries;
    }

    /**
     * Gets the maximum number of batteries this component can hold.
     *
     * @return the maximum number of batteries
     */
    public int getMaximumBatteries() {
        return maximumBatteries;
    }

    /**
     * Sets the current number of batteries and notifies observers of the change.
     *
     * @param numberOfBatteries the new number of batteries
     */
    public void setNumberOfBatteries(int numberOfBatteries) {
        this.numberOfBatteries = numberOfBatteries;
        notifyObservers();
    }

    /**
     * Gets the current number of batteries stored in this component.
     *
     * @return the current number of batteries
     */
    public int getNumberOfBatteries() {
        return numberOfBatteries;
    }

    /**
     * Creates and returns the JavaFX node representation of this battery component
     * along with its associated controller. Uses lazy initialization to cache the
     * node and controller pair for subsequent calls.
     *
     * @return a Pair containing the JavaFX Node and its ComponentController,
     *         or null if an IOException occurs during FXML loading
     */
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

    /**
     * Draws a line of the Text User Interface (TUI) representation for this battery component.
     * The battery is displayed with current/maximum battery count in the middle line.
     *
     * @param line the line number to draw (0-2, where 1 is the middle line showing battery count)
     * @return a string representation of the specified line for TUI display
     * @throws IndexOutOfBoundsException if the line parameter is not 0, 1, or 2
     */
    @Override
    public String drawLineTui(int line) throws IndexOutOfBoundsException {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + (green + numberOfBatteries + "/" + maximumBatteries + reset) + " " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Returns the type of this component for identification purposes.
     *
     * @return ComponentTypeView.BATTERY indicating this is a battery component
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.BATTERY;
    }

    /**
     * Creates a deep copy of this BatteryView instance.
     * The cloned object will have the same ID, connectors, clockwise orientation,
     * maximum batteries, current number of batteries, and wrong state as the original.
     *
     * @return a new BatteryView instance that is a copy of this object
     */
    @Override
    public BatteryView clone() {
        BatteryView copy = new BatteryView(this.getID(), this.getConnectors(), this.getClockWise(), this.getMaximumBatteries());
        copy.setNumberOfBatteries(this.numberOfBatteries);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
