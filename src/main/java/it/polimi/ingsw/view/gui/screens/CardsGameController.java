package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.dice.RollDice;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseCannons;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseEngines;
import it.polimi.ingsw.event.game.clientToServer.energyUse.UseShield;
import it.polimi.ingsw.event.game.clientToServer.goods.ExchangeGoods;
import it.polimi.ingsw.event.game.clientToServer.goods.SwapGoods;
import it.polimi.ingsw.event.game.clientToServer.planets.SelectPlanet;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.player.GiveUp;
import it.polimi.ingsw.event.game.clientToServer.player.Play;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ChooseFragment;
import it.polimi.ingsw.event.game.clientToServer.spaceship.SetPenaltyLoss;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.components.CabinController;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.AbandonedStationView;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.cards.PlanetsView;
import it.polimi.ingsw.view.miniModel.cards.SmugglersView;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
import it.polimi.ingsw.view.miniModel.components.StorageView;
import it.polimi.ingsw.view.miniModel.good.GoodView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.javatuples.Triplet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CardsGameController implements MiniModelObserver, Initializable {

    @FXML private StackPane parent;
    @FXML private Group resizeGroup;
    @FXML private VBox mainVBox;
    @FXML private StackPane currentCard;
    @FXML private StackPane board;
    @FXML private StackPane clientSpaceShip;
    @FXML private HBox lowerHBox;

    private final double ORIGINAL_MAIN_BOX_WIDTH = 1600;
    private final double ORIGINAL_MAIN_BOX_HEIGHT = 900;

    private StackPane newOtherPlayerPane;

    private StackPane newSelectGoodsPane;

    private StackPane newPlanetPane;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Integer> selectedBatteriesList = new ArrayList<>();
    private final List<Integer> selectedCannonsList = new ArrayList<>();
    private final List<Integer> selectedEnginesList = new ArrayList<>();
    private final List<Integer> selectedCabinsList = new ArrayList<>();

    private final List<Integer> penaltyGoods = new ArrayList<>();

    // Exchange goods
    private final List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges = new ArrayList<>();
    private final List<Integer> goodsToGet = new ArrayList<>();
    private final List<Integer> goodsToLeave = new ArrayList<>();
    private int storageID;
    private final List<GoodView> cardGoods = new ArrayList<>();
    private boolean changeCardGoods = true;
    private int deckLen = -1;

    // Swap goods
    private int fromStorage = -1;
    private final ArrayList<Integer> fromList = new ArrayList<>();
    private int withStorage = -1;
    private final ArrayList<Integer> withList = new ArrayList<>();

    private int totalButtons = 19;

    private enum ListType {
        CANNONS,
        ENGINES,
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL imageUrl = getClass().getResource("/image/background/background2.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }

        StackPane.setAlignment(mainVBox, Pos.CENTER);

        ChangeListener<Number> resizeListener = createResizeListener();
        parent.widthProperty().addListener(resizeListener);
        parent.heightProperty().addListener(resizeListener);

        mainVBox.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    resizeListener.changed(null, null, null);
                    newScene.windowProperty().addListener((_, _, newWin) -> {
                        if (newWin != null) {
                            Platform.runLater(() -> resizeListener.changed(null, null, null));
                        }
                    });
                });
            }
        });
    }

    private ChangeListener<Number> createResizeListener() {
        return (_, _, _) -> {
            if (parent.getWidth() <= 0 || parent.getHeight() <= 0) {
                return;
            }

            double scaleX = parent.getWidth() / ORIGINAL_MAIN_BOX_WIDTH;
            double scaleY = parent.getHeight() / ORIGINAL_MAIN_BOX_HEIGHT;
            double scale = Math.min(scaleX, scaleY);

            resizeGroup.setScaleX(scale);
            resizeGroup.setScaleY(scale);
        };
    }

    @Override
    public void react() {
        Platform.runLater(() -> {

            if (deckLen == -1) {
                deckLen = mm.getShuffledDeckView().getDeck().size();
                resetHandlers();
            }
            else {
                if (deckLen != mm.getShuffledDeckView().getDeck().size()) {
                    changeCardGoods = true;
                    resetHandlers();
                }
            }

            lowerHBox.getChildren().clear();

            CardView card = mm.getShuffledDeckView().getDeck().peek();
            totalButtons = 2+ mm.getOtherPlayers().size();

            switch (card.getCardViewType()) {
                case ABANDONEDSTATION:
                    totalButtons += 1 + 4;
                    activeAcceptButton();
                    activeGoodsButtons();
                    break;
                case SMUGGLERS:
                    totalButtons += 3 + 2 + 4 + 3;
                    activeCannonsButton();
                    activeBatteriesButtons();
                    activeGoodsButtons();
                    activePenaltyGoods();
                    break;
                case METEORSSWARM:
                    totalButtons += 1 + 1 + 1 + 2;
                    activeFragmentsButtons();
                    activeShieldButtons();
                    activeBatteriesButtons();
                    activeRollDiceButtons();
                    break;
                case PIRATES:
                    totalButtons += 1 + 1 + 1 + 3 + 2;
                    activeFragmentsButtons();
                    activeShieldButtons();
                    activeBatteriesButtons();
                    activeRollDiceButtons();
                    activeCannonsButton();
                    break;
                case SLAVERS:
                    totalButtons += 3 + 3 + 2;
                    activeCannonsButton();
                    activeBatteriesButtons();
                    activeCabinsButtons();
                    break;
                case EPIDEMIC:
                    break;
                case STARDUST:
                    break;
                case OPENSPACE:
                    totalButtons += 3 + 2;
                    activeEnginesButtons();
                    activeBatteriesButtons();
                    break;
                case COMBATZONE:
                    totalButtons += 3 + 3 + 1 + 1 + 1 + 3 + 3 + 2;
                    activeCannonsButton();
                    activeEnginesButtons();
                    activeBatteriesButtons();
                    activeRollDiceButtons();
                    activeShieldButtons();
                    activeFragmentsButtons();
                    activePenaltyGoods();
                    activeCabinsButtons();
                    break;
                case PLANETS:
                    totalButtons += 1 + 4;
                    activeSelectPlanetButton();
                    activeGoodsButtons();
                    break;
                case ABANDONEDSHIP:
                    totalButtons *= 1 + 3;
                    activeAcceptButton();
                    activeCabinsButtons();
                    break;
            }


            // Exchange goods
            if (changeCardGoods) {
                card = mm.getShuffledDeckView().getDeck().peek();
                switch (card.getCardViewType()) {
                    case PLANETS:
                        cardGoods.addAll(((PlanetsView) card).getPlanet(((PlanetsView) card).getPlanetSelected()));
                        break;
                    case SMUGGLERS:
                        cardGoods.addAll(((SmugglersView) card).getGoods());
                        break;
                    case ABANDONEDSTATION:
                        cardGoods.addAll(((AbandonedStationView) card).getGoods());
                        break;
                }
                changeCardGoods = false;
            }

            activeEndTurnButtons();
            activeGiveUp();

            for (PlayerDataView player : mm.getOtherPlayers()) {
                Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
                otherButtonPlayer.setOnMouseClicked(_ -> showOtherPlayer(player));
                otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
                lowerHBox.getChildren().add(otherButtonPlayer);
            }

            clientSpaceShip.getChildren().clear();
            clientSpaceShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());

            board.getChildren().clear();
            board.getChildren().add(mm.getBoardView().getNode().getValue0());

            currentCard.getChildren().clear();
            currentCard.getChildren().add(mm.getShuffledDeckView().getDeck().peek().getNode().getValue0());
        });
    }

    private void setEffectCabins() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null && component.getType() == ComponentTypeView.CABIN) {
                    Node node = component.getNode().getValue0();

                    DropShadow redGlow = new DropShadow();
                    redGlow.setColor(Color.BLUE);
                    redGlow.setRadius(20);
                    redGlow.setSpread(0.6);

                    Glow glow = new Glow(0.7);
                    glow.setInput(redGlow);

                    node.setEffect(glow);

                    CabinController cabinController = (CabinController) component.getNode().getValue1();

                    node.setOnMouseClicked(_ -> {
                        selectedCabinsList.add(component.getID());
                        cabinController.setOpacity();
                    });
                }
            }
        }
    }

    private void setEffectStorages(int mode) {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    if (component.getType() == ComponentTypeView.STORAGE) {
                        Node node = component.getNode().getValue0();

                        DropShadow redGlow = new DropShadow();
                        redGlow.setColor(Color.WHITE);
                        redGlow.setRadius(20);
                        redGlow.setSpread(0.6);

                        Glow glow = new Glow(0.7);
                        glow.setInput(redGlow);

                        node.setEffect(glow);

                        showGoodsToSelect(((StorageView) component), mode);
                    }
                }
            }
        }
    }

    private void showGoodsToSelect(StorageView storage, int mode) {
        createGoodsToSelectOptionsPane(storage, mode);

        Platform.runLater(() -> {
            newSelectGoodsPane.setVisible(true);
            newSelectGoodsPane.toFront();
            parent.layout();

            newSelectGoodsPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newSelectGoodsPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void createGoodsToSelectOptionsPane(StorageView storage, int mode) {
        newSelectGoodsPane = new StackPane();
        newSelectGoodsPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newSelectGoodsPane, Pos.CENTER);

        newSelectGoodsPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newSelectGoodsPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        VBox newSelectGoodsVBox = new VBox(15);
        newSelectGoodsVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newSelectGoodsVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newSelectGoodsVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newSelectGoodsVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newSelectGoodsVBox.minWidthProperty().bind(newSelectGoodsVBox.prefWidthProperty());
        newSelectGoodsVBox.minHeightProperty().bind(newSelectGoodsVBox.prefHeightProperty());
        newSelectGoodsVBox.maxWidthProperty().bind(newSelectGoodsVBox.prefWidthProperty());
        newSelectGoodsVBox.maxHeightProperty().bind(newSelectGoodsVBox.prefHeightProperty());


        if (mode == 0 || mode == 1) {
            // Create a title label with a drop shadow effect
            Label titleLabel = new Label("Select goods:");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            titleLabel.setEffect(new DropShadow());

            VBox goods = new VBox(15);
            for (GoodView good : storage.getGoods()) {
                if (good != null) {
                    CheckBox goodCheck = new CheckBox(good.toString());
                    goods.getChildren().add(goodCheck);
                }
            }

            goods.setMaxWidth(newSelectGoodsVBox.getMaxWidth() * 0.8);

            // Buttons box to hold the confirm and cancel buttons
            HBox buttonsBox = new HBox(15);
            buttonsBox.setAlignment(Pos.CENTER);

            // Create confirm button
            Button confirmButton = new Button("Create");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            confirmButton.setOnMouseClicked(_ -> {
                if (mode == 0) {
                    fromStorage = storage.getID();
                    int i = 0;
                    for (Node node : goods.getChildren()) {
                        if (node instanceof CheckBox check && check.isSelected()) {
                            fromList.add(storage.getGoods()[i].getValue());
                        }
                        i++;
                    }
                } else {
                    withStorage = storage.getID();
                    int i = 0;
                    for (Node node : goods.getChildren()) {
                        if (node instanceof CheckBox check && check.isSelected()) {
                            withList.add(storage.getGoods()[i].getValue());
                        }
                        i++;
                    }
                }
            });
            // Create cancel button
            Button cancelButton = new Button("Cancel");
            cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            cancelButton.setOnAction(_ -> hideOptions(newSelectGoodsPane));

            buttonsBox.getChildren().addAll(confirmButton, cancelButton);

            // Add all components to the VBox
            newSelectGoodsVBox.getChildren().addAll(titleLabel,
                    goods,
                    buttonsBox);
        }

        if (mode == 2) {
            // Create a title label with a drop shadow effect
            Label titleLabel = new Label("Select goods to DROP:");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            titleLabel.setEffect(new DropShadow());

            VBox goods = new VBox(15);
            for (GoodView good : storage.getGoods()) {
                if (good != null) {
                    CheckBox goodCheck = new CheckBox(good.toString());
                    goods.getChildren().add(goodCheck);
                }
            }

            goods.setMaxWidth(newSelectGoodsVBox.getMaxWidth() * 0.8);

            // Buttons box to hold the confirm and cancel buttons
            HBox buttonsBox = new HBox(15);
            buttonsBox.setAlignment(Pos.CENTER);

            // Create confirm button
            Button confirmButton = new Button("Drop");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            confirmButton.setOnMouseClicked(_ -> {
                storageID = storage.getID();
                int i = 0;
                for (Node node : goods.getChildren()) {
                    if (node instanceof CheckBox check && check.isSelected()) {
                        goodsToLeave.add(i);
                    }
                    i++;
                }
            });
            confirmButton.setOnAction(_ -> {
                hideOptions(newSelectGoodsPane);
                showGoodsToSelect(null, 3);
            });

            buttonsBox.getChildren().addAll(confirmButton);

            // Add all components to the VBox
            newSelectGoodsVBox.getChildren().addAll(titleLabel,
                    goods,
                    buttonsBox);
        }

        if (mode == 3) {
            // Create a title label with a drop shadow effect
            Label titleLabel = new Label("Select goods to GET:");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            titleLabel.setEffect(new DropShadow());

            VBox goods = new VBox(15);
            for (GoodView good : cardGoods) {
                if (good != null) {
                    CheckBox goodCheck = new CheckBox(good.toString());
                    goods.getChildren().add(goodCheck);
                }
            }

            goods.setMaxWidth(newSelectGoodsVBox.getMaxWidth() * 0.8);

            // Buttons box to hold the confirm and cancel buttons
            HBox buttonsBox = new HBox(15);
            buttonsBox.setAlignment(Pos.CENTER);

            // Create confirm button
            Button confirmButton = new Button("Pick");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            confirmButton.setOnMouseClicked(_ -> {
                int i = 0;
                for (Node node : goods.getChildren()) {
                    if (node instanceof CheckBox check && check.isSelected()) {
                        goodsToGet.add(cardGoods.get(i).getValue());
                    }
                    i++;
                }

                exchanges.add(new Triplet<>(goodsToGet, goodsToLeave, storageID));
                StatusEvent status = ExchangeGoods.requester(Client.transceiver, new Object()).request(new ExchangeGoods(mm.getUserID(), exchanges));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                }
            });
            confirmButton.setOnAction(_ -> hideOptions(newSelectGoodsPane));

            buttonsBox.getChildren().addAll(confirmButton);

            // Add all components to the VBox
            newSelectGoodsVBox.getChildren().addAll(titleLabel,
                    goods,
                    buttonsBox);
        }

        if (mode == 4) {
            // Create a title label with a drop shadow effect
            Label titleLabel = new Label("Select goods to GET:");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            titleLabel.setEffect(new DropShadow());

            VBox goods = new VBox(15);
            for (GoodView good : storage.getGoods()) {
                if (good != null) {
                    CheckBox goodCheck = new CheckBox(good.toString());
                    goods.getChildren().add(goodCheck);
                }
            }

            goods.setMaxWidth(newSelectGoodsVBox.getMaxWidth() * 0.8);

            // Buttons box to hold the confirm and cancel buttons
            HBox buttonsBox = new HBox(15);
            buttonsBox.setAlignment(Pos.CENTER);

            // Create confirm button
            Button confirmButton = new Button("Pick");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            confirmButton.setOnMouseClicked(_ -> {
                for (Node node : goods.getChildren()) {
                    if (node instanceof CheckBox check && check.isSelected()) {
                        penaltyGoods.add(storage.getID());
                    }
                }
            });
            confirmButton.setOnAction(_ -> hideOptions(newSelectGoodsPane));

            buttonsBox.getChildren().addAll(confirmButton);

            // Add all components to the VBox
            newSelectGoodsVBox.getChildren().addAll(titleLabel,
                    goods,
                    buttonsBox);
        }

        newSelectGoodsPane.getChildren().add(newSelectGoodsVBox);
        StackPane.setAlignment(newSelectGoodsVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newSelectGoodsPane);
        newSelectGoodsPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newSelectGoodsPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newSelectGoodsPane.toFront();
            parent.layout();
            //updateNewLobbyOptionsSizes();
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

                    node.setOnMouseClicked(_ -> {
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
            case CANNONS ->  selectedCannonsList;
            case ENGINES -> selectedEnginesList;
        };

        Color color = switch (type) {
            case CANNONS ->  Color.PURPLE;
            case ENGINES -> Color.YELLOW;
        };

        ComponentTypeView componentTypeView = switch (type) {
            case CANNONS ->  ComponentTypeView.DOUBLE_CANNON;
            case ENGINES -> ComponentTypeView.DOUBLE_ENGINE;
        };

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

                        node.setOnMouseClicked(_ -> {
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
        newOtherPlayerPane = new StackPane();
        newOtherPlayerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        newOtherPlayerPane.prefWidthProperty().bind(parent.widthProperty());
        newOtherPlayerPane.prefHeightProperty().bind(parent.heightProperty());
        newOtherPlayerPane.maxWidthProperty().bind(parent.widthProperty());
        newOtherPlayerPane.maxHeightProperty().bind(parent.heightProperty());

        VBox newOtherPlayerVBox = new VBox(15);
        newOtherPlayerVBox.setAlignment(Pos.CENTER);

        newOtherPlayerVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.8));
        newOtherPlayerVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.8));
        newOtherPlayerVBox.maxWidthProperty().bind(mainVBox.widthProperty().multiply(0.8));
        newOtherPlayerVBox.maxHeightProperty().bind(mainVBox.heightProperty().multiply(0.8));
        newOtherPlayerVBox.setStyle("-fx-background-color: transparent;");

        Label titleLabel = new Label(player.getUsername() + "'s spaceship");
        titleLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #f2ff00;");
        titleLabel.setEffect(new DropShadow());


        StackPane otherShip = new StackPane();
        otherShip.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.6));
        otherShip.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        otherShip.setStyle("-fx-background-color: transparent;");
        otherShip.getChildren().add(player.getShip().getNode().getValue0());

        Button backButton = new Button("Back");
        backButton.setPrefSize(200, 60);
        backButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 10;");
        backButton.setOnAction(_ -> hideOptions(newOtherPlayerPane));

        newOtherPlayerVBox.getChildren().addAll(titleLabel, otherShip, backButton);
        newOtherPlayerPane.getChildren().add(newOtherPlayerVBox);
        StackPane.setAlignment(newOtherPlayerVBox, Pos.CENTER);

        parent.getChildren().add(newOtherPlayerPane);
        newOtherPlayerPane.setVisible(false);

        Platform.runLater(() -> {

            newOtherPlayerPane.setVisible(true);
            newOtherPlayerPane.toFront();

            parent.layout();
            //updateNewLobbyOptionsSizes();
        });
    }

    private void hideOptions(StackPane pane) {
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

    private void error(StatusEvent status) {
        Stage currentStage = (Stage) parent.getScene().getWindow();
        MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
        resetHandlers();
        selectedEnginesList.clear();
        selectedBatteriesList.clear();
        selectedCabinsList.clear();
        selectedCannonsList.clear();
        fromStorage = -1;
        withStorage = -1;
        fromList.clear();
        withList.clear();
        cardGoods.clear();
        penaltyGoods.clear();
    }
    
    private void showPlanetOptions(PlanetsView planets) {
        createPlanetPane(planets);

        Platform.runLater(() -> {
            newPlanetPane.setVisible(true);
            newPlanetPane.toFront();
            parent.layout();

            newPlanetPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newPlanetPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    private void createPlanetPane(PlanetsView planets) {
        newPlanetPane = new StackPane();
        newPlanetPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newPlanetPane, Pos.CENTER);

        newPlanetPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newPlanetPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        VBox newPlanetVBox = new VBox(15);
        newPlanetVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newPlanetVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newPlanetVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newPlanetVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newPlanetVBox.minWidthProperty().bind(newPlanetVBox.prefWidthProperty());
        newPlanetVBox.minHeightProperty().bind(newPlanetVBox.prefHeightProperty());
        newPlanetVBox.maxWidthProperty().bind(newPlanetVBox.prefWidthProperty());
        newPlanetVBox.maxHeightProperty().bind(newPlanetVBox.prefHeightProperty());


        // Create a title label with a drop shadow effect
        Label titleLabel = new Label("Seelct planet");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());


        // ComboBox for level selection
        ComboBox<Integer> plantNumbers = new ComboBox<>();
        for (int i = 0; i < planets.getNumberOfPlanets(); i++) {
            plantNumbers.getItems().add(i + 1);
        }
        plantNumbers.setValue(1);
        plantNumbers.setPromptText("Select planets");
        plantNumbers.setMaxWidth(newPlanetVBox.getMaxWidth() * 0.8);

        // Buttons box to hold the confirm and cancel buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create confirm button
        Button confirmButton = new Button("Create");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnMouseClicked(_ -> {
            StatusEvent status = SelectPlanet.requester(Client.transceiver, new Object()).request(new SelectPlanet(mm.getUserID(), plantNumbers.getValue()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });
        confirmButton.setOnAction(_ -> hideOptions(newPlanetPane));

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideOptions(newPlanetPane));

        buttonsBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all components to the VBox
        newPlanetVBox.getChildren().addAll(titleLabel,
                new Label("Select planet"),
                plantNumbers,
                buttonsBox);

        newPlanetPane.getChildren().add(newPlanetVBox);
        StackPane.setAlignment(newPlanetVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newPlanetPane);
        newPlanetPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newPlanetPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newPlanetPane.toFront();
            parent.layout();

            newOtherPlayerPane.setOpacity(0);

            FadeTransition fadeInContent = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
            fadeInContent.setFromValue(0);
            fadeInContent.setToValue(1);

            fadeInContent.play();
        });
    }

    private void activeCannonsButton() {
        // Cannons buttons
        Button selectCannonsButton = new Button("Select cannons");
        selectCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button cancelCannonsButton = new Button("Cancel cannons");
        cancelCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button activeCannonsButton = new Button("Active cannons");
        activeCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Select cannons to send
        selectCannonsButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setEffectGeneral(ListType.CANNONS);
        });

        // Cancel cannons to send
        cancelCannonsButton.setOnMouseClicked(_ -> {
            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null && component.getType() == ComponentTypeView.DOUBLE_CANNON) {
                        Node node = component.getNode().getValue0();
                        node.setOnMouseClicked(null);
                        node.setOpacity(1.0);
                        node.setEffect(null);
                    }
                }
            }
            selectedCannonsList.clear();
        });

        // Use cannons event
        activeCannonsButton.setOnMouseClicked(_ -> {
            StatusEvent status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(mm.getUserID(), selectedCannonsList, selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        activeCannonsButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(selectCannonsButton, cancelCannonsButton, activeCannonsButton);
    }

    private void activeEnginesButtons() {
        // Engines buttons
        Button selectEnginesButton = new Button("Select engines");
        selectEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button cancelEnginesButton = new Button("Cancel engines");
        cancelEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button activeEnginesButton = new Button("Active engines");
        activeEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Select engines to send
        selectEnginesButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setEffectGeneral(ListType.ENGINES);
        });

        // Cancel engines to send
        cancelEnginesButton.setOnMouseClicked(_ -> {
            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null && component.getType() == ComponentTypeView.DOUBLE_ENGINE) {
                        Node node = component.getNode().getValue0();
                        node.setOnMouseClicked(null);
                        node.setOpacity(1.0);
                        node.setEffect(null);
                    }
                }
            }
            selectedEnginesList.clear();
        });

        // Use engine event
        activeEnginesButton.setOnMouseClicked(_ -> {
            StatusEvent status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(mm.getUserID(), selectedEnginesList, selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });


        activeEnginesButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(selectEnginesButton, cancelEnginesButton);
    }

    private void activeBatteriesButtons() {
        // Batteries buttons
        Button selectBatteriesButton = new Button("Select Batteries");
        selectBatteriesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button cancelBatteriesButton = new Button("Cancel Batteries");
        cancelBatteriesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Select batteries to send
        selectBatteriesButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setEffectBattery();
        });

        // Cancel batteries to send
        cancelBatteriesButton.setOnMouseClicked(_ -> {
            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null && component.getType() == ComponentTypeView.BATTERY) {
                        Node node = component.getNode().getValue0();
                        node.setOnMouseClicked(null);
                        node.setOpacity(1.0);
                        node.setEffect(null);
                        BatteryController batteryController = (BatteryController) component.getNode().getValue1();
                        batteryController.removeOpacity();
                    }
                }
            }
            selectedBatteriesList.clear();
        });

        cancelBatteriesButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(selectBatteriesButton, cancelBatteriesButton);
    }

    private void activeCabinsButtons() {
        // Cabins buttons
        Button selectCabinsButton = new Button("Select cabins");
        selectCabinsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        Button cancelCabinsButton = new Button("Cancel crew selected");
        cancelCabinsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        Button sendCrewPenalty = new Button("Send crew");
        sendCrewPenalty.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Select crew members to send
        selectCabinsButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setEffectCabins();
        });

        // Cancel crew to send
        cancelCabinsButton.setOnMouseClicked(_ -> {
            for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
                for (ComponentView component : row) {
                    if (component != null && component.getType() == ComponentTypeView.CABIN) {
                        Node node = component.getNode().getValue0();
                        node.setOnMouseClicked(null);
                        node.setOpacity(1.0);
                        node.setEffect(null);
                        CabinController cabinController = (CabinController) component.getNode().getValue1();
                        cabinController.removeOpacity();
                    }
                }
            }
            selectedCabinsList.clear();
        });

        sendCrewPenalty.setOnMouseClicked(_ -> {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 2, selectedCabinsList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        cancelCabinsButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(selectCabinsButton, cancelCabinsButton, sendCrewPenalty);
    }

    private void activeShieldButtons() {
        Button activeShield = new Button("Active shield");
        activeShield.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Use shield event
        activeShield.setOnMouseClicked(_ -> {
            StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(mm.getUserID(), selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        activeShield.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(activeShield);
    }

    private void activeRollDiceButtons() {
        // Roll dice
        Button rollDiceButton = new Button("Roll Dice");
        rollDiceButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Roll dice event
        rollDiceButton.setOnMouseClicked(_ -> {
            StatusEvent status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        rollDiceButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(rollDiceButton);
    }

    private void activeEndTurnButtons() {
        // EndTurn
        Button endTurn = new Button("End turn");
        endTurn.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // EndTurn event
        endTurn.setOnMouseClicked(_ -> {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showInfoMessage(currentStage, "You have end your turn");
            }
        });

        endTurn.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(endTurn);
    }

    public void activeGoodsButtons() {
        // ExchangeGoods
        Button exchangeGoods = new Button("Exchange goods");
        exchangeGoods.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        // Swap goods
        Button fromStorageButton = new Button("Storage to swap from");
        fromStorageButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        Button withStorageButton = new Button("Storage to swap with");
        withStorageButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        Button swapGoods = new Button("Swap selected goods");
        swapGoods.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        exchangeGoods.setOnMouseClicked(_ -> setEffectStorages(2));

        // Swaps from storage
        fromStorageButton.setOnMouseClicked(_ -> setEffectStorages(0));

        // Select with storage
        withStorageButton.setOnMouseClicked(_ -> setEffectStorages(1));

        swapGoods.setOnMouseClicked(_ -> {
            StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(new SwapGoods(mm.getUserID(), fromStorage, withStorage, fromList, withList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        swapGoods.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(exchangeGoods, fromStorageButton, withStorageButton, swapGoods);
    }

    private void activeSelectPlanetButton() {
        // Select planets
        Button selectPlanetButton = new Button("Select planet");
        selectPlanetButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        selectPlanetButton.setOnMouseClicked(_ -> showPlanetOptions(((PlanetsView) mm.getShuffledDeckView().getDeck().peek())));


        selectPlanetButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(selectPlanetButton);
    }

    private void activeAcceptButton() {
        // AcceptCard
        Button acceptCard = new Button("Accept card");
        acceptCard.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        acceptCard.setOnMouseClicked(_ -> {
            StatusEvent status = Play.requester(Client.transceiver, new Object()).request(new Play(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        acceptCard.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(acceptCard);
    }

    private void activePenaltyGoods() {
        Button choosePenaltyGoods = new Button("Choose penalty goods");
        choosePenaltyGoods.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        Button sendGoodsPenalty = new Button("Send penalty goods");
        sendGoodsPenalty.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        Button sendPenaltyBatteries = new Button("Send penalty batteries");
        sendPenaltyBatteries.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        sendGoodsPenalty.setOnMouseClicked(_ -> {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 0, penaltyGoods));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        choosePenaltyGoods.setOnMouseClicked(_ -> setEffectStorages(4));

        sendPenaltyBatteries.setOnMouseClicked(_ -> {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 1, selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        choosePenaltyGoods.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        sendGoodsPenalty.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        sendPenaltyBatteries.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
        lowerHBox.getChildren().addAll(choosePenaltyGoods, sendGoodsPenalty, sendPenaltyBatteries);
    }

    private void activeFragmentsButtons() {
        Button chooseFragments = new Button("Choose fragments");
        chooseFragments.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        chooseFragments.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");

        chooseFragments.setOnMouseClicked(_ -> {
            List<Color> colors = new ArrayList<>();
            colors.add(Color.RED);
            colors.add(Color.GREEN);
            colors.add(Color.BLUE);
            colors.add(Color.YELLOW);
            colors.add(Color.ORANGE);
            colors.add(Color.PURPLE);
            colors.add(Color.PINK);
            colors.add(Color.BROWN);
            colors.add(Color.GRAY);
            colors.add(Color.BLACK);
            colors.add(Color.WHITE);
            colors.add(Color.CYAN);
            colors.add(Color.MAGENTA);
            colors.add(Color.LIME);
            colors.add(Color.OLIVE);
            colors.add(Color.NAVY);
            colors.add(Color.TEAL);
            colors.add(Color.MAROON);
            colors.add(Color.AQUA);
            colors.add(Color.GOLD);
            colors.add(Color.SILVER);
            colors.add(Color.CORAL);
            colors.add(Color.INDIGO);
            colors.add(Color.VIOLET);
            colors.add(Color.KHAKI);
            colors.add(Color.TURQUOISE);
            colors.add(Color.SALMON);
            int i = 0;
            for (List<Pair<Integer, Integer>> group : mm.getClientPlayer().getShip().getFragments()) {
                for (Pair<Integer, Integer> pair : group) {
                    Node node = mm.getClientPlayer().getShip().getComponent(pair.getValue0(), pair.getValue1()).getNode().getValue0();

                    DropShadow redGlow = new DropShadow();
                    redGlow.setColor(colors.get(i));
                    redGlow.setRadius(20);
                    redGlow.setSpread(0.6);

                    Glow glow = new Glow(0.7);
                    glow.setInput(redGlow);

                    node.setEffect(glow);

                    int finalI = i;
                    node.setOnMouseClicked(_ -> {
                        StatusEvent status = ChooseFragment.requester(Client.transceiver, new Object()).request(new ChooseFragment(mm.getUserID(), finalI));
                        if (status.get().equals(mm.getErrorCode())) {
                            error(status);
                        }
                    });
                }
                i++;
            }
        });
    }

    private void activeGiveUp() {
        Button giveUp = new Button("Give up");
        giveUp.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        giveUp.setOnMouseClicked(_ -> {
            StatusEvent status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
        });

        lowerHBox.getChildren().addAll(giveUp);
    }
}
