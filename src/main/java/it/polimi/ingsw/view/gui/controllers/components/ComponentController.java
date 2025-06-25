package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure the parent StackPane can resize properly
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the ImageView to the parent StackPane's size
        componentImage.fitWidthProperty().bind(parent.widthProperty());
        componentImage.fitHeightProperty().bind(parent.heightProperty());

        // Initialize the component image view if needed
        componentImage.setOnDragDetected(event -> {
            Dragboard db = componentImage.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(String.valueOf(componentView.getID()));
            db.setContent(content);

            db.setDragView(componentImage.snapshot(null, null),
                           event.getX(), event.getY());

            event.consume();
        });
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

    @Override
    public void react() {
        Platform.runLater(() -> {
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
        });
    }
}
