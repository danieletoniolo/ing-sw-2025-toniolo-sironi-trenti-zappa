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

public class StorageController extends ComponentController implements Initializable {

    @FXML private HBox storagePane;

    private static final Image REDCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/redCargo.png")).toExternalForm());
    private static final Image YELLOWCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/yellowCargo.png")).toExternalForm());
    private static final Image GREENCARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/greenCargo.png")).toExternalForm());
    private static final Image BLUECARGO = new Image(Objects.requireNonNull(CabinController.class.getResource("/image/misc/blueCargo.png")).toExternalForm());

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
            StorageView storage = ((StorageView) super.componentView);

            storagePane.getChildren().clear();
            storagePane.setSpacing(0);

            for (GoodView good : storage.getGoods()) {
                if (good != null) {
                    ImageView goodView;
                    goodView = switch (good) {
                        case RED -> new ImageView(REDCARGO);
                        case YELLOW -> new ImageView(YELLOWCARGO);
                        case GREEN -> new ImageView(GREENCARGO);
                        case BLUE -> new ImageView(BLUECARGO);
                    };

                    goodView.setPreserveRatio(true);
                    goodView.fitWidthProperty().bind(componentImage.fitWidthProperty().divide(5));

                    storagePane.getChildren().add(goodView);
                }
            }

            //cabinPane.setRotate(super.componentView.getClockWise() * 90);
        });
    }

    public void setOpacity(GoodView good) {
        Platform.runLater(() -> {
            for (int i = 0; i < storagePane.getChildren().size(); i++) {
                Node node = storagePane.getChildren().get(i);
                GoodView goodView = ((StorageView) super.componentView).getGoods()[i];
                if (node.getOpacity() == 1.0 && goodView == good) {
                    node.setOpacity(0.5);
                    break;
                }
            }
        });
    }

    public void removeOpacity() {
        Platform.runLater(() -> {
            for (Node node : storagePane.getChildren()) {
                node.setOpacity(1.0);
            }
        });
    }
}
