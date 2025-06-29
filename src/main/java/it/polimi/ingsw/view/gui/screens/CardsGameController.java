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
import it.polimi.ingsw.view.miniModel.board.LevelView;
import it.polimi.ingsw.view.miniModel.cards.*;
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
import javafx.util.Duration;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CardsGameController implements MiniModelObserver, Initializable {
    private enum ActionState {
        RESET,
        WAITING,
        GIVE_UP,
        ROLL_DICE,
        PROTECTION_NOT_POSSIBLE,
        PROTECTION_NOT_REQUIRED,
        SELECT_ACCEPT,
        SELECT_BATTERIES,
        SELECT_CANNONS,
        SELECT_ENGINES,
        SELECT_PLANET,
        SELECT_GOODS,
        SELECT_SHIELD,
        SELECT_FRAGMENT,
        DISCARD_CABINS,
        DISCARD_GOODS,
        DISCARD_BATTERIES
    };

    private enum ActionOnBatteries {
        DISCARD,
        SELECTION,
        SELECTION_FOR_SHIELD
    }

    static private ActionState actionState = ActionState.RESET;
    private ActionOnBatteries actionOnBatteries = ActionOnBatteries.SELECTION;

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

    private ArrayList<Button> onScreenButtons = new ArrayList<>();

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
                resetEffects();
            }
            else {
                if (deckLen != mm.getShuffledDeckView().getDeck().size()) {
                    changeCardGoods = true;
                    deckLen = mm.getShuffledDeckView().getDeck().size();
                    resetHandlers();
                    resetEffects();
                }
            }

            onScreenButtons.clear();

            CardView card = mm.getShuffledDeckView().getDeck().peek();
            switch (actionState) {
                case SELECT_ENGINES:
                    activeEnginesButtons();
                    activeBatteriesButtons(ActionOnBatteries.SELECTION);
                    break;
                case SELECT_CANNONS:
                    activeCannonsButton();
                    activeBatteriesButtons(ActionOnBatteries.SELECTION);
                    break;
                case SELECT_BATTERIES:
                    // TODO: to remove
                    activeEndTurnButtons();
                    break;
                case SELECT_ACCEPT:
                    activeAcceptButton(() -> {
                        resetActionState();

                        switch (card.getCardViewType()) {
                            case PLANETS -> actionPlanets();
                            case ABANDONEDSHIP -> actionCabins();
                            case SMUGGLERS, ABANDONEDSTATION -> actionAddGoods();
                        }

                        react();

                        displayMessageInfo("You can now select a planet to play.");
                    });
                    activeEndTurnButtons();
                    break;
                case SELECT_PLANET:
                    activeSelectPlanetButton();
                    activeEndTurnButtons();
                    break;
                case SELECT_GOODS:
                    activeGoodsButtons();
                    activeEndTurnButtons();
                    break;
                case SELECT_SHIELD:
                    activeBatteriesButtons(ActionOnBatteries.SELECTION_FOR_SHIELD);
                    activeShieldButtons();
                    break;
                case SELECT_FRAGMENT:
                    activeFragmentsButtons();
                case DISCARD_GOODS:
                    activePenaltyGoods();
                    if (card.getCardViewType() != CardViewType.SLAVERS) {
                        break;
                    }
                case DISCARD_BATTERIES:
                    activeBatteriesButtons(ActionOnBatteries.DISCARD);
                    activeEndTurnButtons();
                    break;
                case DISCARD_CABINS:
                    activeCabinsButtons();
                    break;
                case ROLL_DICE:
                    activeRollDiceButtons();
                    break;
                case PROTECTION_NOT_POSSIBLE:
                    activeCantProtectButtons();
                    break;
                case RESET:
                case PROTECTION_NOT_REQUIRED:
                    activeEndTurnButtons();
                    break;
                /*
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
                case COMBATZONE:
                    if (MiniModel.getInstance().getShuffledDeckView().getDeck().peek().getLevel() == 1) {
                        switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                            case 1 -> {
                                activeEnginesButtons();
                                activeBatteriesButtons();
                            }
                            case 2 -> {
                                activeCannonsButton();
                                activeBatteriesButtons();
                            }
                            default -> {}
                        }
                    }
                    else {
                        switch (((CombatZoneView) MiniModel.getInstance().getShuffledDeckView().getDeck().peek()).getCont()) {
                            case 0 -> {
                                activeCannonsButton();
                                activeBatteriesButtons();
                            }
                            case 1 -> {
                                activeEnginesButtons();
                                activeBatteriesButtons();
                            }
                            default -> {
                            }
                        }
                    }
                    totalButtons += 5;
                    totalButtons += 3 + 3 + 1 + 1 + 1;
                    activeRollDiceButtons();
                    activeShieldButtons();
                    activeFragmentsButtons();
                    activePenaltyGoods();
                    activeCabinsButtons();
                    break;
                    */
            }

            if (changeCardGoods) {
                cardGoods.clear();
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

            if (actionState != ActionState.WAITING) {
                activeGiveUpButton();
            }

            for (PlayerDataView player : mm.getOtherPlayers()) {
                Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
                otherButtonPlayer.setOnMouseClicked(_ -> showOtherPlayer(player));
                onScreenButtons.add(otherButtonPlayer);
            }

            showButtons();

            clientSpaceShip.getChildren().clear();
            clientSpaceShip.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());

            board.getChildren().clear();
            board.getChildren().add(mm.getBoardView().getNode().getValue0());

            currentCard.getChildren().clear();
            currentCard.getChildren().add(mm.getShuffledDeckView().getDeck().peek().getNode().getValue0());
        });
    }

    static public void actionGiveUp() { actionState = ActionState.GIVE_UP; }

    static public void actionRollDice() {
        actionState = ActionState.ROLL_DICE;
    }

    static public void actionCantProtect() {
        actionState = ActionState.PROTECTION_NOT_POSSIBLE;
    }

    static public void actionProtectionNotRequired() {
        actionState = ActionState.PROTECTION_NOT_REQUIRED;
    }

    static public void actionEngine() {
        actionState = ActionState.SELECT_ENGINES;
    }

    static public void actionCannon() {
        actionState = ActionState.SELECT_CANNONS;
    }

    static public void actionShield() {
        actionState = ActionState.SELECT_SHIELD;
    }

    static public void actionBatteries() {
        actionState = ActionState.SELECT_BATTERIES;
    }

    static public void actionPlanets() {
        actionState = ActionState.SELECT_PLANET;
    }

    static public void actionAddGoods() {
        actionState = ActionState.SELECT_GOODS;
    }

    static public void actionDiscardGoods() {
        actionState = ActionState.DISCARD_GOODS;
    }

    static public void actionAccept() {
        actionState = ActionState.SELECT_ACCEPT;
    }

    static public void actionCabins() {
        actionState = ActionState.DISCARD_CABINS;
    }

    static public void actionFragments() {
        actionState = ActionState.SELECT_FRAGMENT;
    }

    static public void waitingActionState() {
        actionState = ActionState.WAITING;
    }

    static public void resetActionState() {
        actionState = ActionState.RESET;
    }

    public void displayMessageInfo(String message) {
        MessageController.showInfoMessage(message);
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

                        node.setOnMouseClicked(_ -> {
                            showGoodsToSelect(((StorageView) component), mode);
                        });
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
        newSelectGoodsVBox.setAlignment(Pos.CENTER);
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
            Label titleLabel = new Label("Select goods from " + (mode == 0 ? "first" : "second") + " cabin:");
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
            Button confirmButton = new Button("Confirm");
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
                    setEffectStorages(1);
                } else {
                    withStorage = storage.getID();
                    int i = 0;
                    for (Node node : goods.getChildren()) {
                        if (node instanceof CheckBox check && check.isSelected()) {
                            withList.add(storage.getGoods()[i].getValue());
                        }
                        i++;
                    }

                    StatusEvent status = SwapGoods.requester(Client.transceiver, new Object()).request(new SwapGoods(mm.getUserID(), fromStorage, withStorage, fromList, withList));
                    fromList.clear();
                    withList.clear();
                    if (status.get().equals(mm.getErrorCode())) {
                        error(status);
                    }
                    else {
                        resetEffects();
                        resetEffects();
                    }

                }
                hideOptions(newSelectGoodsPane);
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
                        i++;
                    }
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
                        i++;
                    }
                }

                exchanges.add(new Triplet<>(goodsToGet, goodsToLeave, storageID));
                StatusEvent status = ExchangeGoods.requester(Client.transceiver, new Object()).request(new ExchangeGoods(mm.getUserID(), exchanges));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                }
                else {
                    resetEffects();
                    resetHandlers();
                }

                List<GoodView> toRemove = new ArrayList<>();
                for (Integer good : goodsToGet) {
                    toRemove.add(GoodView.fromValue(good));
                }
                cardGoods.removeAll(toRemove);

                exchanges.clear();
                goodsToLeave.clear();
                goodsToGet.clear();
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

    private void setBatteriesHandleEffect() {
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

                    displayMessageInfo("Battery: " + selectedBatteriesList);
                }
            }
        }
    }

    private void setCannonsEnginesHandlerEffect(ListType type) {
        List<Integer> IDs = switch (type) {
            case CANNONS -> selectedCannonsList;
            case ENGINES -> selectedEnginesList;
        };

        Color color = switch (type) {
            case CANNONS -> Color.PURPLE;
            case ENGINES -> Color.YELLOW;
        };

        ComponentTypeView componentTypeView = switch (type) {
            case CANNONS -> ComponentTypeView.DOUBLE_CANNON;
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
        resetHandlersBatteries();
        resetHandlersCannons();
        resetHandlersEngines();
        resetHandlersCabins();
        resetHandlersStorages();
    }

    private void resetEffects() {
        resetEffectCannons();
        resetEffectEngines();
        resetEffectBatteries();
        resetEffectCabins();
    }

    // Reset effects for components
    private void resetEffectCannons() {
        mm.getClientPlayer().getShip().getMapDoubleCannons().values().forEach(cannon -> {
            Node node = cannon.getNode().getValue0();
            node.setOpacity(1.0);
        });
        selectedCannonsList.clear();
    }

    private void resetEffectEngines() {
        mm.getClientPlayer().getShip().getMapDoubleEngines().values().forEach(engine -> {
            Node node = engine.getNode().getValue0();
            node.setOpacity(1.0);
        });
        selectedEnginesList.clear();
    }

    private void resetEffectBatteries() {
        mm.getClientPlayer().getShip().getMapBatteries().values().forEach(battery -> {
            BatteryController batteryController = (BatteryController) battery.getNode().getValue1();
            batteryController.removeOpacity();
        });
        selectedBatteriesList.clear();
    }

    private void resetEffectCabins() {
        mm.getClientPlayer().getShip().getMapCabins().values().forEach(cabin -> {
            CabinController cabinController = (CabinController) cabin.getNode().getValue1();
            cabinController.removeOpacity();
        });
        selectedCabinsList.clear();
    }

    // Reset handlers
    private void resetHandlersCannons() {
        mm.getClientPlayer().getShip().getMapDoubleCannons().values().forEach(cannon -> {
            Node node = cannon.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    private void resetHandlersEngines() {
        mm.getClientPlayer().getShip().getMapDoubleEngines().values().forEach(engine -> {
            Node node = engine.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    private void resetHandlersBatteries() {
        mm.getClientPlayer().getShip().getMapBatteries().values().forEach(battery -> {
            Node node = battery.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    private void resetHandlersCabins() {
        mm.getClientPlayer().getShip().getMapCabins().values().forEach(cabin -> {
            Node node = cabin.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    private void resetHandlersStorages() {
        mm.getClientPlayer().getShip().getMapStorages().values().forEach(storage -> {
            Node node = storage.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    private void error(StatusEvent status) {
        MessageController.showErrorMessage(((Pota) status).errorMessage());
        resetEffects();
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
        newPlanetVBox.setAlignment(Pos.CENTER);
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
        Label titleLabel = new Label("Select planet");
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
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnMouseClicked(_ -> {
            StatusEvent status = SelectPlanet.requester(Client.transceiver, new Object()).request(new SelectPlanet(mm.getUserID(), plantNumbers.getValue() - 1));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            } else {
                resetEffects();
                resetHandlers();
                resetActionState();
                actionAddGoods();
                react();

                displayMessageInfo("Now you can swap or exchange goods!");
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

            newPlanetPane.setOpacity(0);

            FadeTransition fadeInContent = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
            fadeInContent.setFromValue(0);
            fadeInContent.setToValue(1);

            fadeInContent.play();
        });
    }

    private void activeCannonsButton() {
        // Cannons buttons
        Button selectCannonsButton = new Button("Select cannons");
        Button cancelCannonsButton = new Button("Cancel cannons");

        // Select cannons to send
        selectCannonsButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setCannonsEnginesHandlerEffect(ListType.CANNONS);
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

        onScreenButtons.add(selectCannonsButton);
        onScreenButtons.add(cancelCannonsButton);
    }

    private void activeEnginesButtons() {
        // Engines buttons
        Button selectEnginesButton = new Button("Select engines");
        Button cancelEnginesButton = new Button("Cancel engines");

        // Select engines to send
        selectEnginesButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setCannonsEnginesHandlerEffect(ListType.ENGINES);
        });

        // Cancel engines to send
        cancelEnginesButton.setOnMouseClicked(_ -> {
            selectedEnginesList.clear();
            resetHandlers();
        });

        onScreenButtons.addAll(List.of(selectEnginesButton, cancelEnginesButton));
    }

    private void activeBatteriesButtons(ActionOnBatteries actionOnBatteries) {
        // Batteries buttons
        Button selectBatteriesButton = new Button("Select batteries");
        Button cancelBatteriesButton = new Button("Cancel Batteries");
        Button useBatteriesButton = null;
        Button sendPenaltyBatteries = null;

        switch (actionOnBatteries) {
            case SELECTION -> {
                useBatteriesButton = new Button("Active");
                // Use engine event
                useBatteriesButton.setOnMouseClicked(_ -> {
                    CardView card = mm.getShuffledDeckView().getDeck().peek();
                    StatusEvent status = null;
                    switch (card.getCardViewType()) {
                        case SLAVERS:
                        case SMUGGLERS:
                        case PIRATES:
                        case OPENSPACE:
                            LevelView level = mm.getBoardView().getLevel();
                            int combatZonePhase = mm.getCombatZonePhase();
                            if ((combatZonePhase == 0 && level == LevelView.SECOND) ||
                                (combatZonePhase == 2 && level == LevelView.LEARNING) ||
                                (card.getCardViewType() != CardViewType.OPENSPACE)) {
                                status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(mm.getUserID(), selectedCannonsList, selectedBatteriesList));
                                break;
                            }
                        case COMBATZONE:
                            status = UseEngines.requester(Client.transceiver, new Object()).request(new UseEngines(mm.getUserID(), selectedEnginesList, selectedBatteriesList));
                            break;
                    };
                    if (status != null && status.get().equals(mm.getErrorCode())) {
                        error(status);
                    } else {
                        status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                        if (status != null && status.get().equals(mm.getErrorCode())) {
                            error(status);
                        }
                        else {
                            resetHandlers();
                            resetEffects();
                        }
                    }
                });
            }
            case DISCARD -> {
                sendPenaltyBatteries = new Button("Send batteries");
                sendPenaltyBatteries.setOnMouseClicked(_ -> {
                    StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 1, selectedBatteriesList));
                    if (status.get().equals(mm.getErrorCode())) {
                        error(status);
                    } else {
                        resetHandlers();
                        resetEffects();
                    }
                });
            }
        }

        // Select engines or batteries to send
        selectBatteriesButton.setOnMouseClicked(_ -> {
            resetHandlers();
            setBatteriesHandleEffect();
            displayMessageInfo("Now you can select batteries to use! Click on the batteries you want to use");
        });

        // Cancel batteries to send
        cancelBatteriesButton.setOnMouseClicked(_ -> {
            resetEffectBatteries();
            selectedBatteriesList.clear();
        });

        onScreenButtons.add(selectBatteriesButton);
        onScreenButtons.add(cancelBatteriesButton);

        switch (actionOnBatteries) {
            case SELECTION -> onScreenButtons.add(useBatteriesButton);
            case DISCARD -> onScreenButtons.add(sendPenaltyBatteries);
        }
    }

    private void activeCabinsButtons() {
        // Cabins buttons
        Button cancelCabinsButton = new Button("Cancel crew selected");

        Button sendCrewPenalty = new Button("Send crew");

        setEffectCabins();

        // Cancel crew to send
        cancelCabinsButton.setOnMouseClicked(_ -> {
            resetEffectCabins();
            selectedCabinsList.clear();
        });

        sendCrewPenalty.setOnMouseClicked(_ -> {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 2, selectedCabinsList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            } else {
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                } else {
                    resetEffects();
                    resetHandlers();
                }
            }
        });

        onScreenButtons.add(cancelCabinsButton);
        onScreenButtons.add(sendCrewPenalty);
    }

    private void activeShieldButtons() {
        Button activeShield = new Button("Active shield");

        // Use shield event
        activeShield.setOnMouseClicked(_ -> {
            StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(mm.getUserID(), selectedBatteriesList));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                resetHandlers();
                resetEffects();
            }
        });

        onScreenButtons.add(activeShield);
    }

    private void activeRollDiceButtons() {
        // Roll dice
        Button rollDiceButton = new Button("Roll Dice");

        // Roll dice event
        rollDiceButton.setOnMouseClicked(_ -> {
            StatusEvent status = RollDice.requester(Client.transceiver, new Object()).request(new RollDice(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                }
                else {
                    resetHandlers();
                    resetEffects();
                }
            }
        });

        onScreenButtons.add(rollDiceButton);
    }

    private void activeCantProtectButtons() {
        Button cantProtectButton = new Button("End turn");

        // EndTurn event
        cantProtectButton.setOnMouseClicked(_ -> {
            List<Integer> batteries = new ArrayList<>();
            batteries.add(-1);
            StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(MiniModel.getInstance().getUserID(), batteries));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                error(status);
            } else {
                // Player is ready for the next hit, so we end the turn
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(MiniModel.getInstance().getUserID()));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    error(status);
                } else {
                    resetHandlers();
                    resetEffects();
                }
            }

        });

        onScreenButtons.add(cantProtectButton);
    }

    private void activeEndTurnButtons() {
        // EndTurn
        Button endTurn = new Button("End turn");

        // EndTurn event
        endTurn.setOnMouseClicked(_ -> {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                displayMessageInfo("You have end your turn");
            }
        });

        onScreenButtons.add(endTurn);
    }

    public void activeGoodsButtons() {
        // ExchangeGoods
        Button exchangeGoods = new Button("Exchange goods");
        Button swapGoods = new Button("Swap goods");

        exchangeGoods.setOnMouseClicked(_ -> setEffectStorages(2));

        // Swaps from storage
        swapGoods.setOnMouseClicked(_ -> setEffectStorages(0));

        onScreenButtons.add(exchangeGoods);
        onScreenButtons.add(swapGoods);
    }

    private void activeSelectPlanetButton() {
        // Select planets
        Button selectPlanetButton = new Button("Open choose planet menu");

        selectPlanetButton.setOnMouseClicked(_ -> {
            showPlanetOptions(((PlanetsView) mm.getShuffledDeckView().getDeck().peek()));
        });

        onScreenButtons.add(selectPlanetButton);
    }

    private void activeAcceptButton(Runnable onSuccess) {
        Button acceptCard = new Button("Accept");

        acceptCard.setOnMouseClicked(_ -> {
            StatusEvent status = Play.requester(Client.transceiver, new Object()).request(new Play(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            } else {
                resetHandlers();
                resetEffects();
                onSuccess.run();
            }
        });

        onScreenButtons.add(acceptCard);
    }

    private void activePenaltyGoods() {
        Button choosePenaltyGoods = new Button("Choose penalty goods");
        Button sendGoodsPenalty = new Button("Send penalty goods");

        sendGoodsPenalty.setOnMouseClicked(_ -> {
            StatusEvent status = SetPenaltyLoss.requester(Client.transceiver, new Object()).request(new SetPenaltyLoss(mm.getUserID(), 0, penaltyGoods));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                if (status.get().equals(mm.getErrorCode())) {
                    error(status);
                }
                else {
                    resetHandlers();
                    resetEffects();
                }
            }
        });

        choosePenaltyGoods.setOnMouseClicked(_ -> setEffectStorages(4));

        onScreenButtons.add(choosePenaltyGoods);
        onScreenButtons.add(sendGoodsPenalty);
    }

    private void activeFragmentsButtons() {
        Button chooseFragments = new Button("Choose fragments");

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
                        else {
                            status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                            if (status.get().equals(mm.getErrorCode())) {
                                error(status);
                            }
                            else {
                                resetHandlers();
                                resetEffects();
                            }
                        }
                    });
                }
                i++;
            }
        });

        onScreenButtons.add(chooseFragments);
    }

    private void activeGiveUpButton() {
        Button giveUp = new Button("Give up");

        giveUp.setOnMouseClicked(_ -> {
            StatusEvent status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                resetHandlers();
                resetEffects();
            }
        });

        onScreenButtons.add(giveUp);
    }

    private void showButtons() {
        lowerHBox.getChildren().clear();
        for (Button button : onScreenButtons) {
            button.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
            button.prefWidthProperty().bind(lowerHBox.widthProperty().divide(onScreenButtons.size()));
            lowerHBox.getChildren().add(button);
        }
    }
}
