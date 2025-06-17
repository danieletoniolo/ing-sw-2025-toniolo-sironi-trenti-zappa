package it.polimi.ingsw.view.gui.controllers.components;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.ViewablePileView;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;


public class ViewablePileController implements MiniModelObserver {
    private ViewablePileView viewablePileView;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane componentPile;

    public void setComponentPile(ViewablePileView viewablePileView) {
        this.viewablePileView = viewablePileView;
        componentPile.setStyle("-fx-background-color: #3498db;");
        componentPile.setHgap(10);
        componentPile.setVgap(10);
        updateView();
    }

    @Override
    public void onModelChanged() {
        updateView();
    }

    public void updateView() {
        componentPile.getChildren().clear();
        for (ComponentView component : viewablePileView.getViewableComponents()) {
            componentPile.getChildren().add(component.createGuiNode());
        }
    }
}