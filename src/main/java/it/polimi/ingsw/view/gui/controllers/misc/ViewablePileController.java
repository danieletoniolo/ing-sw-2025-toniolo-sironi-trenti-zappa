package it.polimi.ingsw.view.gui.controllers.misc;

import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for a viewable pile in the GUI.
 * Implements the observer pattern to react to changes in the mini model and initializes
 * the related JavaFX components.
 */
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

    /**
     * Initializes the controller after its root element has been completely processed.
     * Binds the width and height of the scroll pane to the parent StackPane,
     * and binds the height of the HBox to the height of the scroll pane.
     *
     * @param url The location used to resolve relative paths for the root object, or null if not known.
     * @param resourceBundle The resources used to localize the root object, or null if not localized.
     */
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

    /**
     * Sets the model for this controller and registers it as an observer.
     * Triggers an initial update of the view.
     *
     * @param viewablePileView The ViewablePileView model to be associated with this controller.
     */
    public void setModel(ViewablePileView viewablePileView) {
        this.viewablePileView = viewablePileView;
        this.viewablePileView.registerObserver(this);
        this.react();
    }


    /**
     * Reacts to changes in the observed mini model.
     * Updates the GUI components on the JavaFX Application Thread.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            try {
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
                        vBox.getChildren().add(component.getNode().getValue0());
                    } else {
                        // If the current VBox is full, add it to the HBox and create a new one
                        vBox.setSpacing(5);
                        vBox.getChildren().add(component.getNode().getValue0());
                        hBox.getChildren().add(vBox);
                        vBox = null;
                    }
                }
                // If there's an unclosed VBox, add it to the HBox
                if (vBox != null) {
                    hBox.getChildren().add(vBox);
                }
            } catch (Exception e) {}
        });
    }

    /**
     * Returns a list of ComponentController instances associated with the components
     * in the current ViewablePileView.
     *
     * @return a list of ComponentController objects for each component in the pile
     */
    public List<ComponentController> getComponentControllers() {
        List<ComponentController> componentControllers = new ArrayList<>();
        for (ComponentView tile : viewablePileView.getViewableComponents()) {
            componentControllers.add(tile.getNode().getValue1());
        }
        return componentControllers;
    }

}
