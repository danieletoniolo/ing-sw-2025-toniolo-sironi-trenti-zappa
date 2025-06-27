package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseEngines;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.gui.controllers.ship.SpaceShipController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
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
    private Button selectBatteriesButton;
    private Button selectEnginesButton;
    private Button selectCabinsButton;
    private Button activeCannonsButton;
    private Button activeEnginesButton;
    private Button activeShield;

    private StackPane newOtherPlayerPane;
    private VBox newOtherPlayerVBox;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Integer> selectedBatteriesList = new ArrayList<>();
    private final List<Integer> selectedCannonsList = new ArrayList<>();
    private final List<Integer> selectedEnginesList = new ArrayList<>();
    private final List<Integer> selectedCabinsList = new ArrayList<>();

    private enum ListType {
        BATTERIES,
        CANNONS,
        ENGINES,
        CABINS
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int totalButtons = 7 + mm.getOtherPlayers().size();

        selectBatteriesButton = new Button("Select Batteries");
        selectCannonsButton = new Button("Select cannons");
        selectEnginesButton = new Button("Select Engines");
        selectCabinsButton = new Button("Select cabins");

        activeCannonsButton = new Button("Active cannons");
        activeEnginesButton = new Button("Active engines");
        activeShield = new Button("Active shield");

        selectBatteriesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        selectCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        selectEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        selectCabinsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        activeCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        activeEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        activeShield.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        lowerHBox.getChildren().addAll(selectBatteriesButton, selectCannonsButton, selectEnginesButton, selectCabinsButton);

        for (PlayerDataView player : mm.getOtherPlayers()) {
            Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
            otherButtonPlayer.setOnMouseClicked(e -> showOtherPlayer(player));
            otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
            lowerHBox.getChildren().add(otherButtonPlayer);
        }
    }


    @Override
    public void react() {
        selectBatteriesButton.setOnMouseClicked(e -> {
            resetEffects();
            activeSelectComponents(ListType.BATTERIES);
        });

        selectCannonsButton.setOnMouseClicked(e -> {
            resetEffects();
            activeSelectComponents(ListType.CANNONS);
        });

        selectEnginesButton.setOnMouseClicked(e -> {
            resetEffects();
            activeSelectComponents(ListType.ENGINES);
        });

        selectCabinsButton.setOnMouseClicked(e -> {
            resetEffects();
            activeSelectComponents(ListType.CABINS);
        });

        activeEnginesButton.setOnMouseClicked(e -> {
            StatusEvent status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(mm.getUserID(), selectedEnginesList, selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            }
        });


        Platform.runLater(() -> {
            clientSpaceShip.getChildren().clear();
            clientSpaceShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());

            board.getChildren().clear();
            board.getChildren().add(mm.getBoardView().getNode().getValue0());

            currentCard.getChildren().clear();
            currentCard.getChildren().add(mm.getShuffledDeckView().getDeck().peek().getNode().getValue0());
        });
    }

    private void activeSelectComponents(ListType type) {
        List<Integer> IDs = switch (type) {
            case BATTERIES -> selectedBatteriesList;
            case CABINS -> selectedCabinsList;
            case CANNONS ->  selectedCannonsList;
            case ENGINES -> selectedEnginesList;
        };

        Color color = switch (type) {
            case BATTERIES -> Color.GREEN;
            case CABINS -> Color.BLUE;
            case CANNONS ->  Color.PURPLE;
            case ENGINES -> Color.YELLOW;
        };

        ComponentTypeView componentTypeView = switch (type) {
            case BATTERIES -> ComponentTypeView.BATTERY;
            case CABINS -> ComponentTypeView.CABIN;
            case CANNONS ->  ComponentTypeView.DOUBLE_CANNON;
            case ENGINES -> ComponentTypeView.DOUBLE_ENGINE;
        };

        SpaceShipController spaceShipController = mm.getClientPlayer().getShip().getNode().getValue1();

//        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
//            for (ComponentView component : row) {
        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            if (component.getComponentView().getType() == componentTypeView) {
                Node node = component.getParent();

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
                    IDs.add(component.getComponentView().getID());
                });

                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showInfoMessage(currentStage, "Battery: " + selectedBatteriesList);
            }
        }
    }

    private void resetEffects() {
        SpaceShipController spaceShipController = mm.getClientPlayer().getShip().getNode().getValue1();

        for (ComponentController component : spaceShipController.getShipComponentControllers()) {
            Node node = component.getParent();
            node.setEffect(null);
        }
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
}
