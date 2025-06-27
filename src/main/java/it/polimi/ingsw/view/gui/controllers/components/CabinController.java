package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.components.CabinView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for managing cabin components in the GUI.
 * This class extends ComponentController and implements Initializable to handle
 * the visual representation and user interactions for cabin components.
 * It manages the display of crew members within the cabin, including their
 * types (human, purple alien, brown alien) and visual effects like opacity changes.
 */
public class CabinController extends ComponentController implements Initializable {

    /**
     * The HBox container that holds the crew members in the cabin.
     */
    @FXML private HBox cabinPane;

    /**
     * Static image resource for human crew members.
     */
    private static final Image HUMAN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/human.png")).toExternalForm());

    /**
     * Static image resource for purple alien crew members.
     */
    private static final Image PURPLEALIEN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/purpleAlien.png")).toExternalForm());

    /**
     * Static image resource for brown alien crew members.
     */
    private static final Image BROWNALIEN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/brownAlien.png")).toExternalForm());

    /**
     * Initializes the cabin controller by setting up the cabin pane properties
     * and binding its dimensions to the component image.
     *
     * @param location The location used to resolve relative paths for the root object, or null if not known
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Ensure the cabinPane can resize properly
        cabinPane.setMinSize(0, 0);
        cabinPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the cabinPane to the componentImage's width
        cabinPane.prefWidthProperty().bind(componentImage.fitWidthProperty());
        cabinPane.prefHeightProperty().bind(componentImage.fitHeightProperty());
    }

    /**
     * Sets the model for this cabin controller and registers it as an observer.
     *
     * @param componentView The component view model to set for this controller
     */
    @Override
    public void setModel(ComponentView componentView){
        super.componentView = componentView;
        super.componentView.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to model changes by updating the cabin's visual representation.
     * This method clears the current crew members and recreates them based on
     * the current model state, including crew number and type.
     */
    @Override
    public void react() {
        super.react();

        Platform.runLater(() -> {
            // Update the batteryPane based on the model's state
            CabinView cabin = ((CabinView) super.componentView);

            cabinPane.getChildren().clear();
            cabinPane.setSpacing(0);

            for (int i = 0; i < cabin.getCrewNumber(); i++) {
                ImageView crew;
                crew = switch (cabin.getCrewType()) {
                    case BROWALIEN -> new ImageView(BROWNALIEN);
                    case PURPLEALIEN -> new ImageView(PURPLEALIEN);
                    default -> new ImageView(HUMAN);
                };

                crew.setPreserveRatio(true);
                crew.fitWidthProperty().bind(componentImage.fitWidthProperty().divide(5));

                cabinPane.getChildren().add(crew);
            }

            //cabinPane.setRotate(super.componentView.getClockWise() * 90);
        });
    }

    /**
     * Sets the opacity of the first fully opaque crew member to 50%.
     * This method is typically used to indicate selection or highlighting
     * of a specific crew member.
     */
    public void setOpacity() {
        Platform.runLater(() -> {
            for (Node node : cabinPane.getChildren()) {
                if (node.getOpacity() == 1.0) {
                    node.setOpacity(0.5);
                    break;
                }
            }
        });
    }

    /**
     * Removes opacity effects from all crew members by setting their
     * opacity back to 100%. This method restores all crew members
     * to their normal visual state.
     */
    public void removeOpacity() {
        Platform.runLater(() -> {
            for (Node node : cabinPane.getChildren()) {
                node.setOpacity(1.0);
            }
        });
    }
}
