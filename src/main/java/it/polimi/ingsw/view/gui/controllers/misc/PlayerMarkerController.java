package it.polimi.ingsw.view.gui.controllers.misc;

import it.polimi.ingsw.view.miniModel.player.MarkerView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Controller for the player marker component in the GUI.
 * Handles initialization and binding of the marker image to its parent container.
 */
public class PlayerMarkerController implements Initializable {

    /** The parent StackPane containing the marker image. */
    @FXML private StackPane parent;

    /** The ImageView displaying the player's marker. */
    @FXML private ImageView markerImage;

    /**
     * Initializes the controller, setting up resizing behavior for the marker image.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure the parent StackPane can resize properly
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the ImageView to the parent StackPane's size
        markerImage.fitWidthProperty().bind(parent.widthProperty());
        markerImage.fitHeightProperty().bind(parent.heightProperty());
    }

    /**
     * Sets the model for the player marker, updating the marker image
     * based on the provided MarkerView enum value.
     *
     * @param markerView the MarkerView enum representing the player's marker color
     */
    public void setModel(MarkerView markerView) {
        String path = switch (markerView) {
            case RED -> "/image/misc/redMarker.png";
            case YELLOW -> "/image/misc/yellowMarker.png";
            case GREEN -> "/image/misc/greenMarker.png";
            case BLUE -> "/image/misc/blueMarker.png";
        };
        Image img = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        markerImage.setImage(img);
    }
}
