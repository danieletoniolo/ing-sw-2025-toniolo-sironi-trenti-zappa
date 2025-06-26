package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.javatuples.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ValidationController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;

    @FXML private VBox mainVBox;

    @FXML private Label titleLabel;

    @FXML private HBox centerHBox;

    @FXML private Button confirmChoiceButton;

    @FXML private StackPane spaceShipStackPane;

    @FXML private HBox lowerHBox;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Pair<Integer, Integer>> componentsToDestroy = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        confirmChoiceButton.setOnMouseClicked(e -> {
            StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(
                    new DestroyComponents(MiniModel.getInstance().getUserID(), componentsToDestroy));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
            else {
                for (ComponentController component : mm.getClientPlayer().getShip().getNode().getValue1().getShipComponentControllers()) {
                    Node node = component.getParent();

                    node.setDisable(false); // disable clicks on the component

                    node.setOpacity(1.0); // Set opacity to indicate selection
                }

                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            }
            componentsToDestroy.clear();
        });
    }

    @Override
    public void react() {
        Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
        SpaceShipController spaceShipController = spaceShipPair.getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            component.getParent().setOnMouseClicked(e -> {
                Node node = component.getParent();

                node.setDisable(true); // disable clicks on the component

                node.setOpacity(0.5); // Set opacity to indicate selection

                componentsToDestroy.add(new Pair<>(component.getComponentView().getRow() - 1, component.getComponentView().getCol() - 1));
            });
        }

        Platform.runLater(() -> {
            spaceShipStackPane.getChildren().clear();
            spaceShipStackPane.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
        });
    }
}
