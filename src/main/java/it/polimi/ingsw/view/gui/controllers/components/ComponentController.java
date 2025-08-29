package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for managing component display and interaction in the GUI.
 *
 * This class serves as a bridge between the component model (ComponentView) and the JavaFX UI components.
 * It handles the visual representation of game components, including image loading, rotation, and responsiveness.
 * The controller implements the observer pattern to react to model changes and uses JavaFX's Initializable
 * interface for proper initialization after FXML loading.
 *
 * Key responsibilities:
 * - Binding component images to their container for responsive scaling
 * - Updating visual representation when the component model changes
 * - Managing component rotation and covered/uncovered states
 * - Providing access to the underlying model and UI elements
 */
public class ComponentController implements MiniModelObserver, Initializable {
    /**
     * The StackPane that serves as the parent container for the component image.
     */
    @FXML protected StackPane parent;

    /**
     * The ImageView that displays the component image.
     */
    @FXML protected ImageView componentImage;

    /**
     * The ComponentView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    protected ComponentView componentView;

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by JavaFX after loading the FXML file.
     *
     * Sets up the parent StackPane to be resizable and binds the component image
     * dimensions to the parent container size for responsive scaling.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure the parent StackPane can resize properly
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the ImageView to the parent StackPane's size
        componentImage.fitWidthProperty().bind(parent.widthProperty());
        componentImage.fitHeightProperty().bind(parent.heightProperty());
    }

    /**
     * Sets the model for this controller and loads the corresponding component image.
     *
     * @param componentView the ComponentView model to set
     * @apiNote This method should be called after the FXML has been loaded
     *          to ensure that the componentImage ImageView is initialized.
     */
    public void setModel(ComponentView componentView) {
        this.componentView = componentView;
        this.componentView.registerObserver(this);

        this.react();
    }

    /**
     * Reacts to changes in the component model by updating the visual representation.
     * This method is called when the ComponentView model notifies its observers of changes.
     * Updates the component image based on whether it's covered or not, and applies rotation.
     *
     * @implNote This method uses Platform.runLater() to ensure UI updates happen on the JavaFX Application Thread
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            try {
                if (componentView.getID() == -1) {
                    return;
                }

                String path;
                // Update the image based on the component model
                if (componentView.isCovered()) {
                    path = "/image/components/covered.jpg";
                } else {
                    path = "/image/components/" + componentView.getID() + ".jpg";
                }
                Image img = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());

                // Check the rotation of the component
                componentImage.setRotate(componentView.getClockWise() * 90);

                // Set the image to the ImageView
                componentImage.setImage(img);
            } catch (Exception e) {}
        });
    }

    /**
     * Returns the parent container of this component.
     *
     * @return the StackPane that serves as the parent container for the component
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Returns the ComponentView model associated with this controller.
     *
     * @return the ComponentView model that contains the component's data and state
     */
    public ComponentView getComponentView() {
        return componentView;
    }
}
