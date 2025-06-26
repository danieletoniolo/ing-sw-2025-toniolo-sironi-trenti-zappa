package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.clientToServer.spaceship.DestroyComponents;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
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

    private StackPane newValidationOptionsPane;

    private VBox newValidationOptionsVBox;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Pair<Integer, Integer>> componentsToDestroy = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void react() {
        confirmChoiceButton.setOnMouseClicked(e -> {
            StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(
                    new DestroyComponents(MiniModel.getInstance().getUserID(), componentsToDestroy));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
            else {
                Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
                SpaceShipController spaceShipController = spaceShipPair.getValue1();

                for (ComponentController component : spaceShipController.getShipComponentControllers()) {
                    Node node = component.getParent();
                    node.setDisable(false); // disable clicks on the component
                    node.setOpacity(1.0); // Set opacity to indicate selection
                    node.setEffect(null);
                }

                for (ComponentController component : spaceShipController.getShipComponentControllers()) {
                    Node node = component.getParent();
                    node.setDisable(false); // disable clicks on the component
                    node.setOpacity(1.0); // Set opacity to indicate selection
                    node.setEffect(null);
                }

                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            }
            componentsToDestroy.clear();
        });

        Pair<Node, SpaceShipController> spaceShipPair = mm.getClientPlayer().getShip().getNode();
        SpaceShipController spaceShipController = spaceShipPair.getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            Node node = component.getParent();

            if (component.getComponentView().getIsWrong()) {
                DropShadow redGlow = new DropShadow();
                redGlow.setColor(Color.RED);
                redGlow.setRadius(20);
                redGlow.setSpread(0.6);

                Glow glow = new Glow(0.7);
                glow.setInput(redGlow);

                node.setEffect(glow);
            } else {
                node.setEffect(null);
            }

            component.getParent().setOnMouseClicked(e -> {
                node.setDisable(true); // disable clicks on the component

                node.setOpacity(0.5); // Set opacity to indicate selection

                componentsToDestroy.add(new Pair<>(component.getComponentView().getRow() - 1, component.getComponentView().getCol() - 1));
            });
        }

        if (mm.getClientPlayer().getShip().getFragments() != null && mm.getClientPlayer().getShip().getFragments().size() > 1) {
            Platform.runLater(this::showValidationChoice);
        }

        Platform.runLater(() -> {
            spaceShipStackPane.getChildren().clear();
            spaceShipStackPane.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
        });
    }

    private void showValidationChoice() {
        if (newValidationOptionsPane == null) {
            createValidationCrewOptionsPane();
        }

        Platform.runLater(() -> {
            newValidationOptionsPane.setVisible(true);
            newValidationOptionsPane.toFront();
            parent.layout();

            newValidationOptionsPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newValidationOptionsPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void createValidationCrewOptionsPane() {
        newValidationOptionsPane = new StackPane();
        newValidationOptionsPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newValidationOptionsPane, Pos.CENTER);

        newValidationOptionsPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newValidationOptionsPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        newValidationOptionsVBox = new VBox(15);
        newValidationOptionsVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newValidationOptionsVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newValidationOptionsVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newValidationOptionsVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newValidationOptionsVBox.minWidthProperty().bind(newValidationOptionsVBox.prefWidthProperty());
        newValidationOptionsVBox.minHeightProperty().bind(newValidationOptionsVBox.prefHeightProperty());
        newValidationOptionsVBox.maxWidthProperty().bind(newValidationOptionsVBox.prefWidthProperty());
        newValidationOptionsVBox.maxHeightProperty().bind(newValidationOptionsVBox.prefHeightProperty());

        // Create a title label with a drop shadow effect
        Label titleLabel = new Label("Select fragments group");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());


        // ComboBox for type crew selection
        ComboBox<Integer> crewType = new ComboBox<>();
        for (int i = 0; i < mm.getClientPlayer().getShip().getFragments().size(); i++) {
            crewType.getItems().add(i + 1);
        }
        crewType.setValue(1);
        crewType.setPromptText("Select fragments:");
        crewType.setMaxWidth(newValidationOptionsVBox.getMaxWidth() * 0.8);

        // Buttons box to hold the confirm and cancel buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnAction(_ -> {
            int fragment = crewType.getValue();
            StatusEvent status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(mm.getUserID(), fragment));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });
        confirmButton.setOnAction(_ -> hideValidationLobbyOptions());

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideValidationLobbyOptions());

        buttonsBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all components to the VBox
        newValidationOptionsVBox.getChildren().addAll(titleLabel,
                new Label("Select fragments group:"),
                crewType,
                buttonsBox);

        newValidationOptionsPane.getChildren().add(newValidationOptionsVBox);
        StackPane.setAlignment(newValidationOptionsVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newValidationOptionsPane);
        newValidationOptionsPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newValidationOptionsPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newValidationOptionsPane.toFront();
            parent.layout();
            //updateNewLobbyOptionsSizes();
        });
    }

    private void hideValidationLobbyOptions() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), newValidationOptionsPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> newValidationOptionsPane.setVisible(false));
        fadeOut.play();
    }
}
