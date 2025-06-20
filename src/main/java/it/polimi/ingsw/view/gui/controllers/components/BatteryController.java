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

    }


}
