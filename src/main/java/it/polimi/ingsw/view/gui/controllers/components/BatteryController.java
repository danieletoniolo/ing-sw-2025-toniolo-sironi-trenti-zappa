package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.components.BatteryView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Controller class for managing battery component UI in the JavaFX application.
 * This class extends ComponentController and implements Initializable to provide
 * functionality for displaying and updating battery energy levels through visual
 * battery icons. It handles the dynamic creation and management of battery icons
 * based on the energy state from the BatteryView model.
 *
 * The controller supports:
 * - Dynamic display of battery icons based on energy level
 * - Visual feedback through opacity changes for energy consumption
 * - Automatic rotation based on component orientation
 * - Observer pattern integration for model updates
 */
public class BatteryController extends ComponentController implements Initializable {
    /**
     * The HBox container that holds the battery icons representing the energy level.
     */
    @FXML private HBox batteryPane;

    /**
     * Static image resource for the battery icon used to display energy units.
     */
    private static final Image BATTERY_IMG = new Image(Objects.requireNonNull(BatteryController.class.getResource("/image/misc/energy.png")).toExternalForm());

    /**
     * Initializes the battery controller component.
     * Sets up the battery pane sizing properties and binds its dimensions
     * to the component image for proper scaling.
     *
     * @param location  the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Ensure the batteryPane can resize properly
        batteryPane.setMinSize(0, 0);
        batteryPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the batteryPane to the componentImage's width
        batteryPane.prefWidthProperty().bind(componentImage.fitWidthProperty());
        batteryPane.prefHeightProperty().bind(componentImage.fitHeightProperty());
    }

    /**
     * Sets the model for this battery controller and establishes the observer pattern.
     * This method binds the controller to a ComponentView model, registers this controller
     * as an observer to receive updates when the model changes, and triggers an initial
     * reaction to update the UI based on the current model state.
     *
     * @param componentView the ComponentView model to be associated with this controller,
     *                     expected to be a BatteryView instance for proper functionality
     */
    @Override
    public void setModel(ComponentView componentView){
        super.componentView = componentView;
        super.componentView.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to changes in the battery model by updating the visual representation.
     * This method is called when the observed BatteryView model notifies of changes.
     * It updates the battery pane with the correct number of battery icons and applies
     * the appropriate rotation based on the component's orientation.
     */
    @Override
    public void react() {
        super.react();

        Platform.runLater(() -> {
            try {
                // Update the batteryPane based on the model's state
                int numberOfBatteries = ((BatteryView) super.componentView).getNumberOfBatteries();

                batteryPane.getChildren().clear();
                batteryPane.setSpacing(0); // Set spacing between battery icons

                for (int i = 0; numberOfBatteries > i; i++) {
                    ImageView battery = new ImageView(BATTERY_IMG);

                    battery.setPreserveRatio(true);
                    battery.fitWidthProperty().bind(componentImage.fitWidthProperty().divide(5));


                    batteryPane.getChildren().add(battery);
                }

                batteryPane.setRotate(super.componentView.getClockWise() * 90);
            } catch (Exception e) {}
        });

    }

    /**
     * Sets the opacity of the first fully opaque battery icon to 0.5.
     * This method is used to visually indicate energy consumption by dimming
     * one battery icon at a time. It only affects the first battery icon
     * found with full opacity (1.0).
     */
    public void setOpacity() {
        Platform.runLater(() -> {
            for (Node node : batteryPane.getChildren()) {
                if (node.getOpacity() == 1.0) {
                    node.setOpacity(0.5);
                    break;
                }
            }
        });
    }

    /**
     * Restores full opacity (1.0) to all battery icons in the battery pane.
     * This method is used to reset the visual state of all battery icons,
     * typically when energy is restored or the component is reset.
     */
    public void removeOpacity() {
        Platform.runLater(() -> {
            for (Node node : batteryPane.getChildren()) {
                node.setOpacity(1.0);
            }
        });
    }
}
