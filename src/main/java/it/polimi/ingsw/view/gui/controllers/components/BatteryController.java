package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.components.BatteryView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;


public class BatteryController extends ComponentController {
    @FXML private HBox batteryPane;

    private static final Image BATTERY_IMG = new Image(Objects.requireNonNull(BatteryController.class.getResource("/image/misc/energy.png")).toExternalForm());


    @Override
    public void setModel(ComponentView componentView){
        super.componentView = componentView;
        super.componentView.registerObserver(this);
        this.react();
    }

    @Override
    public void react() {
        super.react();

        // Update the batteryPane based on the model's state
        int numberOfBacteries = ((BatteryView) super.componentView).getNumberOfBatteries();

        batteryPane.getChildren().clear();
        batteryPane.setSpacing(0); // Set spacing between battery icons

        for (int i = 0; numberOfBacteries > i; i++) {
            ImageView battery = new ImageView(BATTERY_IMG);

            // Fixed dimensions (could be parameterized if needed)
            battery.setFitWidth(50);
            battery.setFitHeight(50);
            batteryPane.getChildren().add(battery);
        }

        batteryPane.toFront();

    }


}
