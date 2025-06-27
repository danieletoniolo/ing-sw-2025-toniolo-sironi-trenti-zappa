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
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
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

    @FXML private StackPane board;

    @FXML private StackPane clientShip;

    @FXML private HBox lowerHBox;

    private StackPane newValidationOptionsPane;
    private VBox newValidationOptionsVBox;

    private StackPane newOtherPlayerPane;
    private VBox newOtherPlayerVBox;

    private Button destroyComponentsButton;
    private Button cancelButton;
    private Button endTurnButton;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Pair<Integer, Integer>> componentsToDestroy = new ArrayList<>();

    private boolean placedMarker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize lower HBox buttons
        int totalButtons = 3 + mm.getOtherPlayers().size();

        destroyComponentsButton = new Button("Destroy");
        cancelButton = new Button("Cancel");
        endTurnButton = new Button("End Turn");

        destroyComponentsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        cancelButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        endTurnButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        lowerHBox.getChildren().addAll(destroyComponentsButton, cancelButton, endTurnButton);

        for (PlayerDataView player : mm.getOtherPlayers()) {
            Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
            otherButtonPlayer.setOnMouseClicked(e -> showOtherPlayer(player));
            otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
            lowerHBox.getChildren().add(otherButtonPlayer);
        }

        placedMarker = true;
    }

    @Override
    public void react() {
        Platform.runLater(() -> {
            resetHandlers();

            destroyComponentsButton.setOnMouseClicked(e -> {
                StatusEvent status = DestroyComponents.requester(Client.transceiver, new Object()).request(new DestroyComponents(mm.getUserID(), componentsToDestroy));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
                for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                    for (ComponentView component : row) {
                        if (component != null) {
                            Node node = component.getNode().getValue0();

                            node.setDisable(false);
                            node.setOpacity(1.0);
                        }
                    }
                }
                componentsToDestroy.clear();
            });

            cancelButton.setOnMouseClicked(e -> {
                for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                    for (ComponentView component : row) {
                        if (component != null) {
                            Node node = component.getNode().getValue0();

                            node.setDisable(false);
                            node.setOpacity(1.0);
                        }
                    }
                }
                componentsToDestroy.clear();
            });

            endTurnButton.setOnMouseClicked(e -> {
                if (!placedMarker) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, "You need to place the marker");
                    return;
                }

                StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });

            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null) {
                        Node node = component.getNode().getValue0();

                        if (component.getIsWrong()) {
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

                        node.setOnMouseClicked(e -> {
                            node.setDisable(true); // disable clicks on the component

                            node.setOpacity(0.5); // Set opacity to indicate selection

                            componentsToDestroy.add(new Pair<>(component.getRow() - 1, component.getCol() - 1));
                        });
                    }
                }
            }

            if (mm.getClientPlayer().getShip().getFragments() != null && mm.getClientPlayer().getShip().getFragments().size() > 1) {
                Platform.runLater(this::showValidationChoice);
                placedMarker = false;
                // TODO: riportare la variabile a true quando faccio evento placeMarker
            }

                board.getChildren().clear();
                board.getChildren().add(mm.getBoardView().getNode().getValue0());

                clientShip.getChildren().clear();
                clientShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
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
        confirmButton.setOnAction(_ -> hideValidationOptions(newValidationOptionsPane));

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideValidationOptions((newValidationOptionsPane)));

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

    private void showOtherPlayer(PlayerDataView player) {
        if (newOtherPlayerPane == null) {
            createOtherPlayerPane(player);
        }

        Platform.runLater(() -> {
            newOtherPlayerPane.setVisible(true);
            newOtherPlayerPane.toFront();
            parent.layout();

            newOtherPlayerPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void createOtherPlayerPane(PlayerDataView player) {
        newOtherPlayerPane = new StackPane();
        newOtherPlayerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newOtherPlayerPane, Pos.CENTER);

        newOtherPlayerPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newOtherPlayerPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        newOtherPlayerVBox = new VBox(15);
        newOtherPlayerVBox.setAlignment(javafx.geometry.Pos.CENTER);

        // Bind the size of the VBox to the main HBox
        newOtherPlayerVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newOtherPlayerVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newOtherPlayerVBox.minWidthProperty().bind(newOtherPlayerVBox.prefWidthProperty());
        newOtherPlayerVBox.minHeightProperty().bind(newOtherPlayerVBox.prefHeightProperty());
        newOtherPlayerVBox.maxWidthProperty().bind(newOtherPlayerVBox.prefWidthProperty());
        newOtherPlayerVBox.maxHeightProperty().bind(newOtherPlayerVBox.prefHeightProperty());

        // Create a title label with a drop shadow effect
        Label titleLabel = new Label(player.getUsername() + "'s spaceship");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());

        // Create StackPane for an other player
        StackPane otherShip = new StackPane();
        otherShip.getChildren().clear();
        otherShip.getChildren().add(player.getShip().getNode().getValue0());
        otherShip.setMaxWidth(newOtherPlayerVBox.getMaxWidth() * 0.8);


        // Create confirm button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOnAction(_ -> hideValidationOptions(newOtherPlayerPane));

        // Add all components to the VBox
        newOtherPlayerVBox.getChildren().addAll(titleLabel,
                otherShip,
                backButton);

        newOtherPlayerPane.getChildren().add(newOtherPlayerVBox);
        StackPane.setAlignment(newOtherPlayerVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newOtherPlayerPane);
        newOtherPlayerPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newOtherPlayerPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newOtherPlayerPane.toFront();
            parent.layout();
            //updateNewLobbyOptionsSizes();
        });
    }

    private void hideValidationOptions(StackPane pane) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> pane.setVisible(false));
        fadeOut.play();
    }

    private void resetHandlers() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    Node node = component.getNode().getValue0();
                    node.setOnMouseClicked(null);
                    node.setEffect(null);
                }
            }
        }
    }
}
