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
import it.polimi.ingsw.event.game.clientToServer.player.Play;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import org.javatuples.Triplet;

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
    private Button cancelCabinsButton;

    private Button activeCannonsButton;
    private Button activeEnginesButton;
    private Button activeShield;

    private Button rollDiceButton;

    private Button endTurn;

    private StackPane newOtherPlayerPane;
    private VBox newOtherPlayerVBox;

    private StackPane newSelectGoodsPane;
    private VBox newSelectGoodsVBox;
    
    private StackPane newPlanetPane;
    private VBox newPlanetVBox;

    private final MiniModel mm = MiniModel.getInstance();
    private final List<Integer> selectedBatteriesList = new ArrayList<>();
    private final List<Integer> selectedCannonsList = new ArrayList<>();
    private final List<Integer> selectedEnginesList = new ArrayList<>();
    private final List<Integer> selectedCabinsList = new ArrayList<>();

    // Exchange goods
    private final List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges = new ArrayList<>();
    private final List<Integer> goodsToGet = new ArrayList<>();
    private final List<Integer> goodsToLeave = new ArrayList<>();
    private int storageID;
    private final List<GoodView> cardGoods = new ArrayList<>();
    private boolean changeCardGoods = true;
    private int deckLen = -1;

    private Button exchangeGoods;

    // Swap goods
    private int fromStorage = -1;
    private ArrayList<Integer> fromList = new ArrayList<>();
    private int withStorage = -1;
    private ArrayList<Integer> withList = new ArrayList<>();

    private Button fromStorageButton;
    private Button withStorageButton;
    private Button swapGoods;

    // Select planets
    private Button selectPlanetButton;

    private Button acceptCard;

    private enum ListType {
        CANNONS,
        ENGINES,
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int totalButtons = 19 + mm.getOtherPlayers().size();

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

        cancelCabinsButton = new Button("Cancel crew selected");
        cancelCabinsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Actions buttons
        activeCannonsButton = new Button("Active cannons");
        activeCannonsButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        activeEnginesButton = new Button("Active engines");
        activeEnginesButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        activeShield = new Button("Active shield");
        activeShield.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Roll dice
        rollDiceButton = new Button("Roll Dice");
        rollDiceButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // EndTurn
        endTurn = new Button("End turn");
        endTurn.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Swap goods
        fromStorageButton = new Button("Storage to swap from");
        fromStorageButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        withStorageButton = new Button("Storage to swap with");
        withStorageButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));
        
        swapGoods = new Button("Swap selected goods");
        swapGoods.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // ExchangeGoods
        exchangeGoods = new Button("Exchange goods");
        exchangeGoods.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // Select planet
        selectPlanetButton = new Button("Select planet");
        selectPlanetButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        // AcceptCard
        acceptCard = new Button("Accept card");
        acceptCard.prefWidthProperty().bind(lowerHBox.widthProperty().divide(totalButtons));

        lowerHBox.getChildren().addAll(selectCannonsButton, cancelCannonsButton, selectEnginesButton,
                cancelEnginesButton, selectBatteriesButton, cancelBatteriesButton, selectCabinsButton,
                cancelCabinsButton, activeCannonsButton, activeEnginesButton, activeShield, fromStorageButton,
                withStorageButton, swapGoods, exchangeGoods, selectPlanetButton, acceptCard, rollDiceButton, endTurn);

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
            fromStorage = -1;
            withStorage = -1;
            fromList.clear();
            withList.clear();
            cardGoods.clear();

            if (deckLen == -1) {
                deckLen = mm.getShuffledDeckView().getDeck().size();
            }
            else {
                if (deckLen != mm.getShuffledDeckView().getDeck().size()) {
                    changeCardGoods = true;
                }
            }

            // Select batteries to send
            selectBatteriesButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectBattery();
            });

            // Cancel batteries to send
            cancelBatteriesButton.setOnMouseClicked(e -> {
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

            // Select cannons to send
            selectCannonsButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectGeneral(ListType.CANNONS);
            });

            // Cancel cannons to send
            cancelCannonsButton.setOnMouseClicked(e -> {
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

            // Select engines to send
            selectEnginesButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectGeneral(ListType.ENGINES);
            });

            // Cancel engines to send
            cancelEnginesButton.setOnMouseClicked(e -> {
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

            // Select crew members to send
            selectCabinsButton.setOnMouseClicked(e -> {
                resetHandlers();
                setEffectCabins();
            });

            // Cancel crew to send
            cancelCabinsButton.setOnMouseClicked(e -> {
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

            // Use engine event
            activeEnginesButton.setOnMouseClicked(e -> {
                StatusEvent status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(mm.getUserID(), selectedEnginesList, selectedBatteriesList));
                error(status);
            });

            // Use cannons event
            activeCannonsButton.setOnMouseClicked(e -> {
                StatusEvent status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(mm.getUserID(), selectedCannonsList, selectedBatteriesList));
                error(status);
            });

            // Use shield event
            activeShield.setOnMouseClicked(e -> {
                StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(mm.getUserID(), selectedBatteriesList));
                error(status);
            });

            // EndTurn event
            endTurn.setOnMouseClicked(e -> {
                StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                error(status);
            });

            // Roll dice event
            rollDiceButton.setOnMouseClicked(e -> {
                StatusEvent status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(mm.getUserID()));
                error(status);
            });

            // Swap
            // Select from storage
            fromStorageButton.setOnMouseClicked(e -> {
                setEffectStorages(0);
            });

            // Select with storage
            withStorageButton.setOnMouseClicked(e -> {
                setEffectStorages(1);
            });
            
            swapGoods.setOnMouseClicked(e -> {
                StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(new SwapGoods(mm.getUserID(), fromStorage, withStorage, fromList, withList));
                error(status);
            });

            // Exchange goods
            if (changeCardGoods) {
                CardView card = mm.getShuffledDeckView().getDeck().peek();
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
            
            selectPlanetButton.setOnMouseClicked(e -> {
                showPlanetOptions(((PlanetsView) mm.getShuffledDeckView().getDeck().peek()));
            });

            exchangeGoods.setOnMouseClicked(e -> {
                setEffectStorages(2);
            });


            acceptCard.setOnMouseClicked(e -> {
                StatusEvent status = Play.requester(Client.transceiver, new Object()).request(new Play(mm.getUserID()));
                error(status);
            });
            
            

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

                    node.setOnMouseClicked(e -> {
                        selectedCabinsList.add(component.getID());
                        cabinController.setOpacity();
                    });

                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showInfoMessage(currentStage, "Cabin: " + selectedCabinsList);
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
        newSelectGoodsVBox = new VBox(15);
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
                            fromList.add(i);
                        }
                        i++;
                    }
                } else {
                    withStorage = storage.getID();
                    int i = 0;
                    for (Node node : goods.getChildren()) {
                        if (node instanceof CheckBox check && check.isSelected()) {
                            withList.add(i);
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
                        goodsToGet.add(i);
                    }
                    i++;
                }

                exchanges.add(new Triplet<>(goodsToGet, goodsToLeave, storageID));
                StatusEvent status = ExchangeGoods.requester(Client.transceiver, new Object()).request(new ExchangeGoods(mm.getUserID(), exchanges));
                error(status);
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
        backButton.setOnAction(_ -> hideOptions(newOtherPlayerPane));

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
        if (status.get().equals(mm.getErrorCode())) {
            Stage currentStage = (Stage) parent.getScene().getWindow();
            MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            react();
        }
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
        newPlanetVBox = new VBox(15);
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
            error(status);
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
            //updateNewLobbyOptionsSizes();
        });
    }
}
