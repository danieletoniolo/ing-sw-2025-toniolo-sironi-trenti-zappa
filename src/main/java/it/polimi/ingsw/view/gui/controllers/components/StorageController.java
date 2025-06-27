package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for managing the visual representation of storage components in the GUI.
 * This class extends ComponentController and implements JavaFX Initializable to handle
 * the display and interaction of storage areas that contain goods represented by colored cargo images.
 *
 * The StorageController is responsible for:
 * - Initializing and configuring the storage pane layout
 * - Reacting to model changes and updating the visual representation
 * - Managing the display of goods as colored cargo images within the storage area
 * - Binding storage pane dimensions to the component image for proper scaling
 */
public class StorageController extends ComponentController implements Initializable {

    /**
     * The HBox container that holds the visual representation of goods in the storage.
     */
    @FXML private HBox storagePane;

    /**
     * Static image for red cargo goods.
     */
    private static final Image REDCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/redCargo.png")).toExternalForm());

    /**
     * Static image for yellow cargo goods.
     */
    private static final Image YELLOWCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/yellowCargo.png")).toExternalForm());

    /**
     * Static image for green cargo goods.
     */
    private static final Image GREENCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/greenCargo.png")).toExternalForm());

    /**
     * Static image for blue cargo goods.
     */
    private static final Image BLUECARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/blueCargo.png")).toExternalForm());

    /**
     * Initializes the storage controller by setting up the storage pane properties
     * and binding its dimensions to the component image.
     *
     * @param location The location used to resolve relative paths for the root object
     * @param resources The resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Ensure the cabinPane can resize properly
        storagePane.setMinSize(0, 0);
        storagePane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the cabinPane to the componentImage's width
        storagePane.prefWidthProperty().bind(componentImage.fitWidthProperty());
        storagePane.prefHeightProperty().bind(componentImage.fitHeightProperty());
    }

    /**
     * Sets the model for this storage controller and registers it as an observer.
     * This method establishes the connection between the controller and the storage view model,
     * enabling the controller to react to changes in the storage state.
     *
     * @param componentView The ComponentView to be set as the model for this controller
     */
    @Override
    public void setModel(ComponentView componentView){
        super.componentView = componentView;
        super.componentView.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to changes in the storage model by updating the visual representation.
     * This method is called whenever the observed storage model changes state.
     * It updates the storage pane by clearing existing goods and adding new ImageViews
     * for each good currently stored, with appropriate colors and sizing.
     */
    @Override
    public void react() {
        super.react();

        Platform.runLater(() -> {
            // Update the storage pane based on the model's state
            StorageView storage = ((StorageView) super.componentView);

            // Clear existing goods and reset spacing
            storagePane.getChildren().clear();
            storagePane.setSpacing(0);

            // Add visual representation for each good in storage
            for (GoodView good : storage.getGoods()) {
                if (good != null) {
                    ImageView goodView;
                    // Create appropriate cargo image based on good color
                    goodView = switch (good) {
                        case RED -> new ImageView(REDCARGO);
                        case YELLOW -> new ImageView(YELLOWCARGO);
                        case GREEN -> new ImageView(GREENCARGO);
                        case BLUE -> new ImageView(BLUECARGO);
                    };

                    // Configure image properties for proper display
                    goodView.setPreserveRatio(true);
                    goodView.fitWidthProperty().bind(componentImage.fitWidthProperty().divide(5));

                    storagePane.getChildren().add(goodView);
                }
            }

            //cabinPane.setRotate(super.componentView.getClockWise() * 90);
        });
    }
}
