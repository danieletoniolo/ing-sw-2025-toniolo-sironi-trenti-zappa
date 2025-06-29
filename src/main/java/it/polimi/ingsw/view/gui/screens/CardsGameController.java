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

/**
 * Controller for the main game screen in the GUI.
 * Handles user interactions, updates the view based on the MiniModel, and manages game actions.
 */
public class CardsGameController implements MiniModelObserver, Initializable {
    /**
     * Represents the possible states of user actions in the game.
     */
    private enum ActionState {
        RESET,
        WAITING,
        FORCE_GIVE_UP,
        ROLL_DICE,
        PROTECTION_NOT_POSSIBLE,
        PROTECTION_NOT_REQUIRED,
        SELECT_ACCEPT,
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

    /**
     * Enum representing the possible actions that can be performed on batteries.
     * DISCARD: Batteries are to be discarded as a penalty.
     * SELECTION: Batteries are being selected for use (e.g., to power engines or cannons).
     * SELECTION_FOR_SHIELD: Batteries are being selected specifically for shield activation.
     */
    private enum ActionOnBatteries {
        DISCARD,
        SELECTION,
        SELECTION_FOR_SHIELD
    }

    /**
     * Current state of the user's action in the game.
     */
    static private ActionState actionState = ActionState.RESET;

    /** Main parent container for the game screen. */
    @FXML private StackPane parent;
    /** Group used for resizing the main content. */
    @FXML private Group resizeGroup;
    /** Main vertical box layout for the game UI. */
    @FXML private VBox mainVBox;
    /** StackPane displaying the current card. */
    @FXML private StackPane currentCard;
    /** StackPane displaying the game board. */
    @FXML private StackPane board;
    /** StackPane displaying the client's spaceship. */
    @FXML private StackPane clientSpaceShip;
    /** HBox containing the lower action buttons. */
    @FXML private HBox lowerHBox;

    private final double ORIGINAL_MAIN_BOX_WIDTH = 1600;
    private final double ORIGINAL_MAIN_BOX_HEIGHT = 900;

    /** Pane for displaying another player's spaceship. */
    private StackPane newOtherPlayerPane;

    /** Pane for selecting goods in the UI. */
    private StackPane newSelectGoodsPane;

    /** Pane for planet selection options. */
    private StackPane newPlanetPane;

    /** Reference to the MiniModel singleton instance. */
    private final MiniModel mm = MiniModel.getInstance();

    /** List of selected battery IDs. */
    private final List<Integer> selectedBatteriesList = new ArrayList<>();

    /** List of selected cannon IDs. */
    private final List<Integer> selectedCannonsList = new ArrayList<>();

    /** List of selected engine IDs. */
    private final List<Integer> selectedEnginesList = new ArrayList<>();

    /** List of selected cabin IDs. */
    private final List<Integer> selectedCabinsList = new ArrayList<>();

    /** List of goods selected as penalty. */
    private final List<Integer> penaltyGoods = new ArrayList<>();

    // Exchange goods

    /** List of exchanges, each as a triplet: goods to get, goods to leave, and storage ID. */
    private final List<Triplet<List<Integer>, List<Integer>, Integer>> exchanges = new ArrayList<>();

    /** List of goods to get during an exchange. */
    private final List<Integer> goodsToGet = new ArrayList<>();

    /** List of goods to leave during an exchange. */
    private final List<Integer> goodsToLeave = new ArrayList<>();

    /** ID of the storage currently being used for exchange. */
    private int storageID;

    /** List of goods currently on the card. */
    private final List<GoodView> cardGoods = new ArrayList<>();

    /** Flag indicating if card goods need to be updated. */
    private boolean changeCardGoods = true;

    /** Current length of the deck, used to detect changes. */
    private int deckLen = -1;

    // Swap goods

    /** ID of the storage to swap from. */
    private int fromStorage = -1;

    /** List of goods to swap from the first storage. */
    private final ArrayList<Integer> fromList = new ArrayList<>();

    /** ID of the storage to swap with. */
    private int withStorage = -1;

    /** List of goods to swap from the second storage. */
    private final ArrayList<Integer> withList = new ArrayList<>();

    /** List of buttons currently displayed on the screen. */
    private ArrayList<Button> onScreenButtons = new ArrayList<>();

    /**
     * Enum representing the type of list for component selection.
     */
    private enum ListType {
        CANNONS,
        ENGINES,
    }

    /**
     * Initializes the main game screen controller.
     * Sets up the background, aligns UI components, and prepares the initial state of the view.
     *
     * @param url The location used to resolve relative paths for the root object, or null if not known.
     * @param resourceBundle The resources used to localize the root object, or null if not localized.
     */
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

    /**
     * Creates a ChangeListener that handles resizing of the main game UI components.
     * The listener adjusts the scale of the resizeGroup based on the parent StackPane's dimensions,
     * maintaining the aspect ratio relative to the original width and height.
     *
     * @return a ChangeListener that updates the UI scale on width or height changes
     */
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

    /**
     * Reacts to changes in the MiniModel and updates the GUI accordingly.
     * This method is called when the observed MiniModel notifies its observers.
     * All UI updates are executed on the JavaFX Application Thread.
     */
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
                    break;
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
                case FORCE_GIVE_UP:
                    activeGiveUpButton(true);
                    break;
                case RESET:
                case PROTECTION_NOT_REQUIRED:
                    activeEndTurnButtons();
                    break;
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

            if (actionState != ActionState.WAITING && actionState != ActionState.FORCE_GIVE_UP) {
                activeGiveUpButton(false);
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

    /**
     * Sets the action state to FORCE_GIVE_UP, indicating the player is giving up.
     */
    static public void actionGiveUp() { actionState = ActionState.FORCE_GIVE_UP; }

    /**
     * Sets the action state to ROLL_DICE, indicating the player should roll the dice.
     */
    static public void actionRollDice() {
        actionState = ActionState.ROLL_DICE;
    }

    /**
     * Sets the action state to PROTECTION_NOT_POSSIBLE, indicating protection is not possible.
     */
    static public void actionCantProtect() {
        actionState = ActionState.PROTECTION_NOT_POSSIBLE;
    }

    /**
     * Sets the action state to PROTECTION_NOT_REQUIRED, indicating protection is not required.
     */
    static public void actionProtectionNotRequired() {
        actionState = ActionState.PROTECTION_NOT_REQUIRED;
    }

    /**
     * Sets the action state to SELECT_ENGINES, indicating the player should select engines.
     */
    static public void actionEngine() {
        actionState = ActionState.SELECT_ENGINES;
    }

    /**
     * Sets the action state to SELECT_CANNONS, indicating the player should select cannons.
     */
    static public void actionCannon() {
        actionState = ActionState.SELECT_CANNONS;
    }

    /**
     * Sets the action state to SELECT_SHIELD, indicating the player should select shield.
     */
    static public void actionShield() {
        actionState = ActionState.SELECT_SHIELD;
    }

    /**
     * Sets the action state to SELECT_PLANET, indicating the player should select a planet.
     */
    static public void actionPlanets() {
        actionState = ActionState.SELECT_PLANET;
    }

    /**
     * Sets the action state to SELECT_GOODS, indicating the player should select goods.
     */
    static public void actionAddGoods() {
        actionState = ActionState.SELECT_GOODS;
    }

    /**
     * Sets the action state to DISCARD_GOODS, indicating the player should discard goods.
     */
    static public void actionDiscardGoods() {
        actionState = ActionState.DISCARD_GOODS;
    }

    /**
     * Sets the action state to SELECT_ACCEPT, indicating the player should accept the current action.
     */
    static public void actionAccept() {
        actionState = ActionState.SELECT_ACCEPT;
    }

    /**
     * Sets the action state to DISCARD_CABINS, indicating the player should discard cabins.
     */
    static public void actionCabins() {
        actionState = ActionState.DISCARD_CABINS;
    }

    /**
     * Sets the action state to DISCARD_BATTERIES, indicating the player should discard batteries.
     */
    static public void actionDiscardBatteries() {
        actionState = ActionState.DISCARD_BATTERIES;
    }

    /**
     * Sets the action state to SELECT_FRAGMENT, indicating the player should select a fragment.
     */
    static public void actionFragments() {
        actionState = ActionState.SELECT_FRAGMENT;
    }

    /**
     * Sets the action state to WAITING, indicating the player is waiting for the next action.
     */
    static public void waitingActionState() {
        actionState = ActionState.WAITING;
    }

    /**
     * Resets the action state to RESET, returning to the default state.
     */
    static public void resetActionState() {
        actionState = ActionState.RESET;
    }

    /**
     * Displays an informational message to the user in the GUI.
     *
     * @param message The message to be shown.
     */
    public void displayMessageInfo(String message) {
        MessageController.showInfoMessage(message);
    }

    /**
     * Highlights all cabin components in the player's spaceship by applying a visual effect.
     * Used to indicate selectable cabins for user interaction.
     */
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

    /**
     * Applies a visual effect to Storage components in the player's spaceship.
     * The behavior and effect applied depend on the mode parameter.
     *
     * @param mode Selection/effect mode to apply to storages.
     */
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

    /**
     * Displays the UI for selecting goods from a given storage, based on the specified mode.
     * This method creates and shows the options pane for the user to interact with goods selection.
     *
     * @param storage the StorageView from which goods can be selected
     * @param mode the selection mode (determines the type of selection UI to display)
     */
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

    /**
     * Creates the options pane for selecting goods from a given storage.
     * The UI and behavior depend on the specified mode.
     *
     * @param storage the StorageView from which goods can be selected
     * @param mode the selection mode (determines the type of selection UI to display)
     */
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
                    CardView card = mm.getShuffledDeckView().getDeck().peek();
                    if (card.getCardViewType() == CardViewType.PLANETS) {
                        cardGoods.clear();
                        cardGoods.addAll(((PlanetsView) card).getPlanet(((PlanetsView) card).getPlanetSelected()));
                    }
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

    /**
     * Applies visual effects and event handlers to the battery components of the player's ship.
     * Used to highlight and enable selection of batteries during gameplay actions.
     */
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

    /**
     * Applies visual effects and event handlers to the cannons or engines components
     * of the player's spaceship, depending on the specified type.
     * Enables interactive selection of these components.
     *
     * @param type The type of component to handle (CANNONS or ENGINES)
     */
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

    /**
     * Displays the spaceship of another player in a new overlay pane.
     * This method creates and shows a StackPane containing the selected player's spaceship,
     * allowing the user to view other players' ships during the game.
     *
     * @param player The PlayerDataView representing the player whose spaceship is to be displayed.
     */
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

    /**
     * Hides the given options pane with a fade-out animation.
     *
     * @param pane The StackPane to hide.
     */
    private void hideOptions(StackPane pane) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> pane.setVisible(false));
        fadeOut.play();
    }

    /**
     * Resets all event handlers for batteries, cannons, engines, and cabins components.
     * This method is typically called to clear previous handlers before setting new ones,
     * ensuring that only the appropriate handlers are active for the current game state.
     */
    private void resetHandlers() {
        resetHandlersBatteries();
        resetHandlersCannons();
        resetHandlersEngines();
        resetHandlersCabins();
        resetHandlersStorages();
    }

    /**
     * Resets all visual effects applied to cannons, engines, batteries, and cabins components.
     * This method is typically called to clear previous effects before applying new ones,
     * ensuring that only the appropriate visual cues are active for the current game state.
     */
    private void resetEffects() {
        resetEffectCannons();
        resetEffectEngines();
        resetEffectBatteries();
        resetEffectCabins();
    }

    /**
     * Resets all visual effects applied to fragment components in the player's spaceship.
     * Iterates through all fragment groups and removes any effect set on their nodes.
     */
    private void resetEffectFragments() {
        for (List<Pair<Integer, Integer>> group : mm.getClientPlayer().getShip().getFragments()) {
            for (Pair<Integer, Integer> pair : group) {
                ComponentView fragment = mm.getClientPlayer().getShip().getSpaceShip()[pair.getValue0()][pair.getValue1()];
                if (fragment != null) {
                    Node node = fragment.getNode().getValue0();
                    node.setOpacity(1.0);
                    node.setOnMouseClicked(null);
                    node.setEffect(null);
                    node.setDisable(false);
                }
            }
        }
    }

    // Reset effects for components
    /**
     * Resets all visual effects applied to double cannon components in the player's spaceship.
     * Iterates through all double cannons and restores their opacity to the default value.
     */
    private void resetEffectCannons() {
        mm.getClientPlayer().getShip().getMapDoubleCannons().values().forEach(cannon -> {
            Node node = cannon.getNode().getValue0();
            node.setOpacity(1.0);
        });
        selectedCannonsList.clear();
    }

    /**
     * Resets all visual effects applied to double engine components in the player's spaceship.
     * Iterates through all double engines and restores their opacity to the default value.
     * Also clears the list of selected engine IDs.
     */
    private void resetEffectEngines() {
        mm.getClientPlayer().getShip().getMapDoubleEngines().values().forEach(engine -> {
            Node node = engine.getNode().getValue0();
            node.setOpacity(1.0);
        });
        selectedEnginesList.clear();
    }

    /**
     * Resets all visual effects applied to battery components in the player's spaceship.
     * Iterates through all batteries and removes any opacity effect set on their nodes.
     */
    private void resetEffectBatteries() {
        mm.getClientPlayer().getShip().getMapBatteries().values().forEach(battery -> {
            BatteryController batteryController = (BatteryController) battery.getNode().getValue1();
            batteryController.removeOpacity();
        });
        selectedBatteriesList.clear();
    }

    /**
     * Resets all visual effects applied to cabin components in the player's spaceship.
     * Iterates through all cabins and restores their default opacity and removes any effect.
     */
    private void resetEffectCabins() {
        mm.getClientPlayer().getShip().getMapCabins().values().forEach(cabin -> {
            CabinController cabinController = (CabinController) cabin.getNode().getValue1();
            cabinController.removeOpacity();
        });
        selectedCabinsList.clear();
    }

    // Reset handlers
    /**
     * Resets all event handlers for double cannon components in the player's spaceship.
     * Iterates through all double cannons and removes any mouse click event handlers and effects.
     */
    private void resetHandlersCannons() {
        mm.getClientPlayer().getShip().getMapDoubleCannons().values().forEach(cannon -> {
            Node node = cannon.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    /**
     * Resets all event handlers for double engine components in the player's spaceship.
     * Iterates through all double engines and removes any mouse click event handlers and effects.
     */
    private void resetHandlersEngines() {
        mm.getClientPlayer().getShip().getMapDoubleEngines().values().forEach(engine -> {
            Node node = engine.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    /**
     * Resets all event handlers for battery components in the player's spaceship.
     * Iterates through all batteries and removes any mouse click event handlers and effects.
     */
    private void resetHandlersBatteries() {
        mm.getClientPlayer().getShip().getMapBatteries().values().forEach(battery -> {
            Node node = battery.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    /**
     * Resets all event handlers for cabin components in the player's spaceship.
     * Iterates through all cabins and removes any mouse click event handlers and effects.
     */
    private void resetHandlersCabins() {
        mm.getClientPlayer().getShip().getMapCabins().values().forEach(cabin -> {
            Node node = cabin.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    /**
     * Resets all event handlers for storage components in the player's spaceship.
     * Iterates through all storages and removes any mouse click event handlers and effects.
     */
    private void resetHandlersStorages() {
        mm.getClientPlayer().getShip().getMapStorages().values().forEach(storage -> {
            Node node = storage.getNode().getValue0();
            node.setOnMouseClicked(null);
            node.setEffect(null);
            node.setDisable(false);
        });
    }

    /**
     * Displays an error message in the GUI using the MessageController.
     * Extracts the error message from the given StatusEvent, casting it to Pota.
     *
     * @param status the StatusEvent containing the error information
     */
    private void error(StatusEvent status) {
        MessageController.showErrorMessage(((Pota) status).errorMessage());
        resetEffects();
    }
    
    /**
     * Displays the planet selection options pane in the GUI.
     * This method creates and shows the pane for choosing a planet from the given PlanetsView.
     *
     * @param planets the PlanetsView containing available planets to select
     */
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

    /**
     * Creates the pane for planet selection options.
     * Initializes a new StackPane with a semi-transparent background for displaying planet choices.
     *
     * @param planets the PlanetsView containing available planets to select
     */
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
                cardGoods.clear();
                CardView card = mm.getShuffledDeckView().getDeck().peek();
                cardGoods.addAll(((PlanetsView) card).getPlanet(((PlanetsView) card).getPlanetSelected()));

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

    /**
     * Activates the buttons for selecting cannons.
     * Creates and manages the buttons to select and cancel the selection of cannons,
     * allowing the user to choose which cannons to use during the turn.
     */
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

    /**
     * Activates the buttons for selecting engines.
     * Creates and manages the buttons to select and cancel the selection of engines,
     * allowing the user to choose which engines to use during the turn.
     */
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

    /**
     * Activates the buttons for battery selection based on the specified action.
     * Creates and manages buttons to select, cancel, or use batteries,
     * adapting the behavior depending on the required action (selection, discard, use for shield).
     *
     * @param actionOnBatteries the type of action to perform on the batteries
     */
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
                        case COMBATZONE:
                            int combatZonePhase = mm.getCombatZonePhase();
                            if ((combatZonePhase == 0 && card.getLevel() == 2) ||
                                (combatZonePhase == 2 && card.getLevel() == 1) ||
                                (card.getCardViewType() != CardViewType.COMBATZONE)) {
                                status = UseCannons.requester(Client.transceiver, new Object()).request(new UseCannons(mm.getUserID(), selectedCannonsList, selectedBatteriesList));
                                break;
                            }
                        case OPENSPACE:
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

    /**
     * Activates the buttons for selecting cabins.
     * Creates and manages the buttons to select and cancel the selection of cabins,
     * allowing the user to choose which cabins to use or discard during the turn.
     */
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

    /**
     * Activates the buttons for shield activation.
     * Creates and manages the button to activate the shield during the player's turn.
     */
    private void activeShieldButtons() {
        Button activeShield = new Button("Active shield");

        // Use shield event
        activeShield.setOnMouseClicked(_ -> {
            if (selectedBatteriesList.isEmpty()) {
                selectedBatteriesList.add(-1);
            }

            StatusEvent status = UseShield.requester(Client.transceiver, new Object()).request(new UseShield(mm.getUserID(), selectedBatteriesList));
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

        onScreenButtons.add(activeShield);
    }

    /**
     * Activates the button(s) for rolling the dice.
     * Creates and manages the UI elements that allow the user to roll dice during their turn.
     */
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

    /**
     * Activates the button for ending the turn when protection is not possible.
     * Creates and displays the "End turn" button for the user to proceed.
     */
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

    /**
     * Activates the button for ending the turn.
     * Creates and displays the "End turn" button for the user to proceed to the next phase.
     */
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

    /**
     * Activates the buttons for selecting goods to exchange or pick up.
     * Creates and manages the UI elements that allow the user to interact with goods during their turn.
     */
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

    /**
     * Activates the button for selecting a planet.
     * Creates and displays the UI element that allows the user to select a planet during their turn.
     */
    private void activeSelectPlanetButton() {
        // Select planets
        Button selectPlanetButton = new Button("Open choose planet menu");

        selectPlanetButton.setOnMouseClicked(_ -> {
            showPlanetOptions(((PlanetsView) mm.getShuffledDeckView().getDeck().peek()));
        });

        onScreenButtons.add(selectPlanetButton);
    }

    /**
     * Activates the accept button for the current card.
     * When the button is pressed, executes the action specified by onSuccess.
     *
     * @param onSuccess Runnable to execute upon successful acceptance.
     */
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

    /**
     * Activates the button for selecting penalty goods.
     * Creates and displays the UI element that allows the user to choose goods to be penalized.
     */
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

    /**
     * Activates the button(s) for selecting fragments.
     * Creates and manages the UI elements that allow the user to choose fragments during their turn.
     */
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
                                resetEffectFragments();
                            }
                        }
                    });
                }
                i++;
            }
        });

        onScreenButtons.add(chooseFragments);
    }

    /**
     * Activates the "Give up" button in the GUI.
     * If forceGiveUp is true, the button will trigger a forced give up action for the player.
     *
     * @param forceGiveUp whether the give up action should be forced
     */
    private void activeGiveUpButton(boolean forceGiveUp) {
        Button giveUp = new Button("Give up");

        giveUp.setOnMouseClicked(_ -> {
            StatusEvent status = GiveUp.requester(Client.transceiver, new Object()).request(new GiveUp(mm.getUserID()));
            if (status.get().equals(mm.getErrorCode())) {
                error(status);
            }
            else {
                if (forceGiveUp) {
                    status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
                    if (status.get().equals(mm.getErrorCode())) {
                        error(status);
                    } else {
                        resetHandlers();
                        resetEffects();
                    }
                } else {
                    resetHandlers();
                    resetEffects();
                }
            }
        });

        onScreenButtons.add(giveUp);
    }

    /**
     * Clears all buttons from the lowerHBox container.
     * This method is typically called before adding new buttons to the UI.
     */
    private void showButtons() {
        lowerHBox.getChildren().clear();
        for (Button button : onScreenButtons) {
            button.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold; textAlignment: CENTER; textFill: white");
            button.prefWidthProperty().bind(lowerHBox.widthProperty().divide(onScreenButtons.size()));
            lowerHBox.getChildren().add(button);
        }
    }
}
