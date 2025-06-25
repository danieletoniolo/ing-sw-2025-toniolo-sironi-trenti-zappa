package it.polimi.ingsw.view.gui.controllers.misc;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.ViewablePileView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewablePileController implements MiniModelObserver, Initializable {

    /**
     * The StackPane that contains the viewable pile.
     */
    @FXML private StackPane parent;

    /**
     * The ScrollPane that allows scrolling through the viewable pile.
     */
    @FXML private ScrollPane scrollPane;

    /**
     * The HBox that contains the components of the viewable pile.
     */
    @FXML private HBox hBox;

    /**
     * The ViewablePileView that represents the viewable pile.
     */
    private ViewablePileView viewablePileView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind the width and height of the scroll pane to the parent StackPane
        scrollPane.prefWidthProperty().bind(parent.widthProperty());
        scrollPane.prefHeightProperty().bind(parent.heightProperty());

        // Bind the height of the HBox to the height of the scroll pane
        hBox.prefHeightProperty().bind(scrollPane.heightProperty().subtract(20));
        // The width should increase as we populate it with components
        hBox.setAlignment(Pos.TOP_LEFT);
    }

    public void setModel(ViewablePileView viewablePileView) {
        this.viewablePileView = viewablePileView;
        this.viewablePileView.registerObserver(this);
        this.react();
    }


    @Override
    public void react() {
        Platform.runLater(() -> {
            // Clear the current components in the scroll pane
            hBox.getChildren().clear();

            List<ComponentView> components = viewablePileView.getViewableComponents();

            VBox vBox = null;
            for (ComponentView component : components) {
                if (vBox == null) {
                    // Create a new VBox for the first component
                    vBox = new VBox();
                    vBox.setAlignment(Pos.CENTER);
                    vBox.prefHeightProperty().bind(hBox.heightProperty());
                    vBox.maxWidthProperty().bind((hBox.heightProperty().divide(2)).subtract(5));
                    vBox.getChildren().add(component.getNode());
                } else {
                    // If the current VBox is full, add it to the HBox and create a new one
                    vBox.setSpacing(5);
                    vBox.getChildren().add(component.getNode());
                    hBox.getChildren().add(vBox);
                    vBox = null;
                }
            }
            // If there's an unclosed VBox, add it to the HBox
            if (vBox != null) {
                hBox.getChildren().add(vBox);
            }
        });
    }
}
