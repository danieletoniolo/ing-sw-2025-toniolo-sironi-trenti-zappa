package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseCannons;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseEngines;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CardsGameController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;
    @FXML private VBox mainVBox;
    @FXML private HBox upperHBox;
    @FXML private VBox upperLeftVBox;
    @FXML private HBox titleCardHBox;
    @FXML private Label titleLabel;
    @FXML private StackPane currentCard;
    @FXML private StackPane board;
    @FXML private VBox upperRightVBox;
    @FXML private StackPane infos;
    @FXML private StackPane clientSpaceShip;
    @FXML private HBox lowerHBox;

    private Button selectCannonsButton;
    private Button cancelCannonsButton;
    private Button selectBatteriesButton;
    private Button cancelBatteriesButton;
    private Button selectEnginesButton;
    private Button cancelEnginesButton;
    private Button selectCabinsButton;

    private Button activeCannonsButton;
    private Button activeEnginesButton;
    private Button activeShield;

    private Button endTurn;

    private StackPane newOtherPlayerPane;
    private VBox newOtherPlayerVBox;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Integer> selectedBatteriesList = new ArrayList<>();
    private final List<Integer> selectedCannonsList = new ArrayList<>();
    private final List<Integer> selectedEnginesList = new ArrayList<>();
    private final List<Integer> selectedCabinsList = new ArrayList<>();

    private enum ListType {
        CANNONS,
        ENGINES,
        CABINS
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int totalButtons = 11 + mm.getOtherPlayers().size();

        // Cannons buttons
        selectCannonsButton = new Button("Select cannons");
        selectCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        cancelCannonsButton = new Button("Cancel cannons");
        cancelCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Engines buttons
        selectEnginesButton = new Button("Select engines");
        selectEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        cancelEnginesButton = new Button("Cancel engines");
        cancelEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Batteries buttons
        selectBatteriesButton = new Button("Select Batteries");
        selectBatteriesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        cancelBatteriesButton = new Button("Cancel Batteries");
        cancelBatteriesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Cabins buttons
        selectCabinsButton = new Button("Select cabins");
        selectCabinsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Actions buttons
        activeCannonsButton = new Button("Active cannons");
        activeCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        activeEnginesButton = new Button("Active engines");
        activeEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        activeShield = new Button("Active shield");
        activeShield.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // EndTurn
        endTurn = new Button("End turn");
        endTurn.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        lowerHBox.getChildren().addAll(selectCannonsButton, cancelCannonsButton, selectEnginesButton, cancelEnginesButton, selectBatteriesButton, cancelBatteriesButton, selectCabinsButton, activeCannonsButton, activeEnginesButton, activeShield, endTurn);

        for (PlayerDataView player : mm.getOtherPlayers()) {
            Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
            otherButtonPlayer.setOnMouseClicked(e -> showOtherPlayer(player));
            otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
            lowerHBox.getChildren().add(otherButtonPlayer);
        }
    }

    @Override
    public void react() {
        Platform.runLater(() -> {
            resetHandlers();
            selectedEnginesList.clear();
            selectedBatteriesList.clear();
            selectedCabinsList.clear();
            selectedCannonsList.clear();

            selectBatteriesButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectBattery();
            });

            cancelBatteriesButton.setOnMouseClicked(e -> {
                resetHandlers();
            });

            selectCannonsButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectGeneral(ListType.CANNONS);
            });

            cancelCannonsButton.setOnMouseClicked(e -> {
                resetHandlers();
            });

            selectEnginesButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectGeneral(ListType.ENGINES);
            });

            cancelEnginesButton.setOnMouseClicked(e -> {
                resetHandlers();
            });

            selectCabinsButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectGeneral(ListType.CABINS);
            });

            // Set active engine button
            activeEnginesButton.setOnMouseClicked(e -> {
                StatusEvent status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(mm.getUserID(), selectedEnginesList, selectedBatteriesList));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });

            activeCannonsButton.setOnMouseClicked(e -> {
                StatusEvent status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(mm.getUserID(), selectedCannonsList, selectedBatteriesList));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });

            activeShield.setOnMouseClicked(e -> {
                StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(mm.getUserID(), selectedBatteriesList));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });

            endTurn.setOnMouseClicked(e -> {
                StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            });

            clientSpaceShip.getChildren().clear();
            clientSpaceShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());

            board.getChildren().clear();
            board.getChildren().add(mm.getBoardView().getNode().getValue0());

            currentCard.getChildren().clear();
            currentCard.getChildren().add(mm.getShuffledDeckView().getDeck().peek().getNode().getValue0());
        });
    }

    private void setEffectBattery() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null && component.getType() == ComponentTypeView.BATTERY) {
                    Node node = component.getNode().getValue0();

                    DropShadow redGlow = new DropShadow();
                    redGlow.setColor(Color.GREEN);
                    redGlow.setRadius(20);
                    redGlow.setSpread(0.6);

                    Glow glow = new Glow(0.7);
                    glow.setInput(redGlow);

                    node.setEffect(glow);

                    BatteryController batteryController = (BatteryController) component.getNode().getValue1();

                    node.setOnMouseClicked(e -> {
                        selectedBatteriesList.add(component.getID());
                        batteryController.setOpacity();
                    });

                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showInfoMessage(currentStage, "Battery: " + selectedBatteriesList);
                }
            }
        }
    }

    private void setEffectGeneral(ListType type) {
        List<Integer> IDs = switch (type) {
            case CABINS -> selectedCabinsList;
            case CANNONS ->  selectedCannonsList;
            case ENGINES -> selectedEnginesList;
        };

        Color color = switch (type) {
            case CABINS -> Color.BLUE;
            case CANNONS ->  Color.PURPLE;
            case ENGINES -> Color.YELLOW;
        };

        ComponentTypeView componentTypeView = switch (type) {
            case CABINS -> ComponentTypeView.CABIN;
            case CANNONS ->  ComponentTypeView.DOUBLE_CANNON;
            case ENGINES -> ComponentTypeView.DOUBLE_ENGINE;
        };

        SpaceShipController spaceShipController = mm.getClientPlayer().getShip().getNode().getValue1();

        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    if (component.getType() == componentTypeView) {
                        Node node = component.getNode().getValue0();

                        DropShadow redGlow = new DropShadow();
                        redGlow.setColor(color);
                        redGlow.setRadius(20);
                        redGlow.setSpread(0.6);

                        Glow glow = new Glow(0.7);
                        glow.setInput(redGlow);

                        node.setEffect(glow);

                        node.setOnMouseClicked(e -> {
                            node.setDisable(true);
                            node.setOpacity(0.5);
                            IDs.add(component.getID());
                        });
                    }
                }
            }
        }
    }

    private void showOtherPlayer(PlayerDataView player) {
        createOtherPlayerPane(player);

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
        backButton.setOnAction(_ -> hideOtherPlayerOptions());

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

    private void hideOtherPlayerOptions() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> newOtherPlayerPane.setVisible(false));
        fadeOut.play();
    }

    private void resetHandlers() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    Node node = component.getNode().getValue0();
                    node.setOnMouseClicked(null);
                    node.setOpacity(1.0);
                    node.setEffect(null);

                    if (component.getType() == ComponentTypeView.BATTERY) {
                        BatteryController batteryController = (BatteryController) component.getNode().getValue1();
                        batteryController.removeOpacity();
                    }
                }
            }
        }
    }
}
