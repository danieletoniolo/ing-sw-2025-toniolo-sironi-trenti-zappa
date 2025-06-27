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

public class CabinController extends ComponentController implements Initializable {

    @FXML private HBox cabinPane;

    private static final Image HUMAN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/human.png")).toExternalForm());
    private static final Image PURPLEALIEN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/purpleAlien.png")).toExternalForm());
    private static final Image BROWNALIEN = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/brownAlien.png")).toExternalForm());

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

    public void removeOpacity() {
        Platform.runLater(() -> {
            for (Node node : cabinPane.getChildren()) {
                node.setOpacity(1.0);
            }
        });
    }
}
