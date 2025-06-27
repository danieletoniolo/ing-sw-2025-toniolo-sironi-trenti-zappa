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


public class BatteryController extends ComponentController implements Initializable {
    @FXML private HBox batteryPane;

    private static final Image BATTERY_IMG = new Image(Objects.requireNonNull(BatteryController.class.getResource("/image/misc/energy.png")).toExternalForm());

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

    @Override
    public void setModel(ComponentView componentView){
        super.componentView = componentView;
        super.componentView.registerObserver(this);
        this.react();
    }

    @Override
    public void react() {
        super.react();

        Platform.runLater(() -> {
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
        });

    }

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

    public void removeOpacity() {
        Platform.runLater(() -> {
            for (Node node : batteryPane.getChildren()) {
                node.setOpacity(1.0);
            }
        });
    }
}
