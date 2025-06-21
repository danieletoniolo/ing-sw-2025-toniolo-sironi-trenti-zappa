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


public class PlayerMarkerController implements Initializable {

    @FXML private StackPane parent;

    @FXML private ImageView markerImage;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure the parent StackPane can resize properly
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the ImageView to the parent StackPane's size
        markerImage.fitWidthProperty().bind(parent.widthProperty());
        markerImage.fitHeightProperty().bind(parent.heightProperty());
    }

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
