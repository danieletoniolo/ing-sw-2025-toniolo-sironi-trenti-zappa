package it.polimi.ingsw.view.gui.controllers.board;

import it.polimi.ingsw.view.gui.controllers.deck.DeckController;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.javatuples.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for managing the board view in the GUI application.
 * This class handles the visual representation and interaction of the game board,
 * including player markers, timer display, and deck management.
 *
 * <p>The controller implements the Observer pattern to react to changes in the board model
 * and provides responsive scaling functionality to adapt to different screen sizes.</p>
 *
 * <p>Key responsibilities include:</p>
 * <ul>
 *   <li>Managing the layout and positioning of board elements (steps, timers, decks)</li>
 *   <li>Updating player marker positions based on game state changes</li>
 *   <li>Providing responsive scaling for different screen resolutions</li>
 *   <li>Coordinating with deck and timer controllers</li>
 * </ul>
 *
 * @see MiniModelObserver
 * @see Initializable
 * @see BoardView
 */
public class BoardController implements MiniModelObserver, Initializable {

    /**
     * The parent StackPane that contains the board group.
     */
    @FXML private StackPane parent;

    /**
     * The Group that contains the board elements, including steps and markers.
     */
    @FXML private Group boardGroup;

    /**
     * The ImageView that displays the background image of the board.
     */
    @FXML private ImageView backgroundImage;

    /**
     * A list of StackPane nodes representing the steps on the board.
     * This list is initialized in the initialize method and sorted based on their IDs.
     * Used to manage the placement of player markers on the board.
     */
    private List<Node> stepsNodes;

    /**
     * A list of Node objects representing timer steps.
     * This is used to display the timer steps on the board.
     */
    private List<Node> timerNodes;

    /**
     * A list of Node objects representing the decks on the board.
     * This is used to display the decks in their respective positions.
     */
    private List<Node> decksNodes;

    /**
     * The BoardView model that this controller observes.
     * It provides the current state of the board, including player positions and steps.
     */
    private BoardView boardView;

    /**
     * The original dimensions of the background image.
     */
    private double ORIGINAL_WIDTH;
    private double ORIGINAL_HEIGHT;

    /**
     * Original dimensions and positions of the background image used for scaling.
     */
    private double ORIGINAL_IMAGE_WIDTH;
    private double ORIGINAL_IMAGE_HEIGHT;
    private double ORIGINAL_IMAGE_X;
    private double ORIGINAL_IMAGE_Y;

    /**
     * Lists to store the original positions and dimensions of steps, timers, and decks.
     */
    private final List<Double> originalStepX = new ArrayList<>();
    private final List<Double> originalStepY = new ArrayList<>();
    private final List<Double> originalStepWidth = new ArrayList<>();
    private final List<Double> originalStepHeight = new ArrayList<>();

    private final List<Double> originalTimerX = new ArrayList<>();
    private final List<Double> originalTimerY = new ArrayList<>();
    private final List<Double> originalTimerWidth = new ArrayList<>();
    private final List<Double> originalTimerHeight = new ArrayList<>();

    private final List<Double> originalDeckX = new ArrayList<>();
    private final List<Double> originalDeckY = new ArrayList<>();
    private final List<Double> originalDeckWidth = new ArrayList<>();
    private final List<Double> originalDeckHeight = new ArrayList<>();

    /**
     * Initializes the board controller by setting up node collections, scaling bindings,
     * and configuring the responsive layout system.
     * <p>
     * This method performs the following operations:
     * 1. Filters and sorts board elements (steps, timers, decks) based on their IDs
     * 2. Sets up default values for original dimensions and positions
     * 3. Creates dynamic scaling bindings for responsive layout
     * 4. Binds all elements to scale with parent container changes
     *
     * @param location The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Filter and sort step nodes (IDs >= 0)
        stepsNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") && Integer.parseInt(node.getId()) >= 0));
        stepsNodes.sort(Comparator.comparingInt(a -> Integer.parseInt(a.getId())));

        // Filter and sort timer nodes (IDs between -3 and -1)
        timerNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") &&
                        Integer.parseInt(node.getId()) >= -3 && Integer.parseInt(node.getId()) <= -1));
        timerNodes.sort((a, b) -> Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId())));

        // Filter and sort deck nodes (IDs between -7 and -4)
        decksNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") &&
                        Integer.parseInt(node.getId()) >= -7 && Integer.parseInt(node.getId()) <= -4));
        decksNodes.sort((a, b) -> Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId())));

        // Store original dimensions and positions for scaling calculations
        setDefaultValues();

        // Disable automatic layout management for manual positioning
        boardGroup.setManaged(false);

        // Create dynamic scaling binding based on the parent container size
        DoubleBinding scaleFactorBinding = Bindings.createDoubleBinding(() -> {
            double parentWidth = parent.getWidth();
            double parentHeight = parent.getHeight();
            if (parentWidth <= 0 || parentHeight <= 0) return 1.0;
            return Math.min(parentWidth / ORIGINAL_WIDTH, parentHeight / ORIGINAL_HEIGHT);
        }, parent.widthProperty(), parent.heightProperty());

        // Bind background image properties to a scale factor for responsive sizing
        backgroundImage.layoutXProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_X));
        backgroundImage.layoutYProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_Y));
        backgroundImage.fitWidthProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_WIDTH));
        backgroundImage.fitHeightProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_HEIGHT));

        // Bind element collections to scaling if they contain nodes
        if (!stepsNodes.isEmpty()) {
            bindStepsElements(scaleFactorBinding);
        }
        if (!timerNodes.isEmpty()) {
            bindTimerElements(scaleFactorBinding);
        }
        if (!decksNodes.isEmpty()) {
            bindDeckElements(scaleFactorBinding);
        }
    }

    /**
     * Sets the default values for original dimensions and positions of all board elements.
     * This method captures the initial state of the board components to be used as reference
     * for scaling calculations. It stores:
     * - Board group dimensions (width and height)
     * - Background image properties (dimensions and position)
     * - Step nodes properties (position and size for each step)
     * - Timer nodes properties (position and size for each timer)
     * - Deck nodes properties (position and size for each deck)
     * <p>
     * This method should be called during initialization before any scaling operations.
     */
    private void setDefaultValues() {
        // Store the original dimensions of the board group for scaling reference
        ORIGINAL_WIDTH = boardGroup.getLayoutBounds().getWidth();
        ORIGINAL_HEIGHT = boardGroup.getLayoutBounds().getHeight();

        // Store the original background image properties
        ORIGINAL_IMAGE_WIDTH = backgroundImage.getFitWidth();
        ORIGINAL_IMAGE_HEIGHT = backgroundImage.getFitHeight();
        ORIGINAL_IMAGE_X = backgroundImage.getLayoutX();
        ORIGINAL_IMAGE_Y = backgroundImage.getLayoutY();

        // Clear existing step position and dimension data
        originalStepX.clear();
        originalStepY.clear();
        originalStepWidth.clear();
        originalStepHeight.clear();

        // Store the original position and dimensions for each step node
        for (Node step : stepsNodes) {
            originalStepX.add(step.getLayoutX());
            originalStepY.add(step.getLayoutY());
            originalStepWidth.add(((StackPane) step).getPrefWidth());
            originalStepHeight.add(((StackPane) step).getPrefHeight());
        }

        // Clear existing timer position and dimension data
        originalTimerX.clear();
        originalTimerY.clear();
        originalTimerWidth.clear();
        originalTimerHeight.clear();

        // Store the original position and dimensions for each timer node
        for (Node timer : timerNodes) {
            originalTimerX.add(timer.getLayoutX());
            originalTimerY.add(timer.getLayoutY());
            originalTimerWidth.add(((StackPane) timer).getPrefWidth());
            originalTimerHeight.add(((StackPane) timer).getPrefHeight());
        }

        // Clear existing deck position and dimension data
        originalDeckX.clear();
        originalDeckY.clear();
        originalDeckWidth.clear();
        originalDeckHeight.clear();

        // Store the original position and dimensions for each deck node
        for (Node deck : decksNodes) {
            originalDeckX.add(deck.getLayoutX());
            originalDeckY.add(deck.getLayoutY());
            originalDeckWidth.add(((StackPane) deck).getPrefWidth());
            originalDeckHeight.add(((StackPane) deck).getPrefHeight());
        }
    }

    /**
     * Binds the layout properties of step elements to a scale factor for responsive resizing.
     * This method applies dynamic scaling to all step nodes by binding their position and size
     * properties to the provided scale factor binding. The scale factor
     * multiplies each step's layout properties to maintain proportional scaling when the parent container
     * size changes.
     *
     * @param scaleFactorBinding the DoubleBinding that calculates the current scale factor
     *                          based on the parent container's size relative to original dimensions
     */
    private void bindStepsElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < stepsNodes.size(); i++) {
            Node step = stepsNodes.get(i);

            step.layoutXProperty().bind(scaleFactorBinding.multiply(originalStepX.get(i)));
            step.layoutYProperty().bind(scaleFactorBinding.multiply(originalStepY.get(i)));
            ((StackPane) step).prefWidthProperty().bind(scaleFactorBinding.multiply(originalStepWidth.get(i)));
            ((StackPane) step).prefHeightProperty().bind(scaleFactorBinding.multiply(originalStepHeight.get(i)));
        }
    }

    /**
     * Binds the layout properties of timer elements to a scale factor for responsive resizing.
     * This method applies dynamic scaling to all timer nodes by binding their position and size
     * properties to the provided scale factor binding. Each timer's layout properties are
     * multiplied by the scale factor to maintain proportional scaling when the parent container
     * size changes.
     *
     * @param scaleFactorBinding the DoubleBinding that calculates the current scale factor
     *                          based on the parent container's size relative to original dimensions
     */
    private void bindTimerElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < timerNodes.size(); i++) {
            Node timer = timerNodes.get(i);

            timer.layoutXProperty().bind(scaleFactorBinding.multiply(originalTimerX.get(i)));
            timer.layoutYProperty().bind(scaleFactorBinding.multiply(originalTimerY.get(i)));
            ((StackPane) timer).prefWidthProperty().bind(scaleFactorBinding.multiply(originalTimerWidth.get(i)));
            ((StackPane) timer).prefHeightProperty().bind(scaleFactorBinding.multiply(originalTimerHeight.get(i)));
        }
    }

    /**
     * Binds the layout properties of deck elements to a scale factor for responsive resizing.
     * This method applies dynamic scaling to all deck nodes by binding their position and size
     * properties to the provided scale factor binding. Each deck's layout properties are
     * multiplied by the scale factor to maintain proportional scaling when the parent container
     * size changes.
     *
     * @param scaleFactorBinding the DoubleBinding that calculates the current scale factor
     *                          based on the parent container's size relative to original dimensions
     */
    private void bindDeckElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < decksNodes.size(); i++) {
            Node deck = decksNodes.get(i);

            deck.layoutXProperty().bind(scaleFactorBinding.multiply(originalDeckX.get(i)));
            deck.layoutYProperty().bind(scaleFactorBinding.multiply(originalDeckY.get(i)));
            ((StackPane) deck).prefWidthProperty().bind(scaleFactorBinding.multiply(originalDeckWidth.get(i)));
            ((StackPane) deck).prefHeightProperty().bind(scaleFactorBinding.multiply(originalDeckHeight.get(i)));
        }
    }

    /**
     * Sets the BoardView model for this controller and establishes the observer relationship.
     * This method assigns the provided BoardView to this controller, registers this controller
     * as an observer of the model to receive updates when the board state changes, and triggers
     * an initial reaction to update the UI with the current model state.
     *
     * @param boardView the BoardView model that this controller will observe and manage
     */
    public void setModel(BoardView boardView) {
        this.boardView = boardView;
        this.boardView.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to changes in the board model by updating the UI components.
     * This method is called when the observed BoardView model notifies of changes.
     * It updates player markers, timer display, and deck representations on the board.
     * <p>
     * The method performs the following operations:
     * 1. Clears all current markers from step nodes
     * 2. Places markers in the waiting area based on number of players
     * 3. Updates player positions on the board according to current game state
     * 4. Updates timer display if present
     * 5. Updates deck representations if present
     * <p>
     * All UI updates are executed on the JavaFX Application Thread using Platform.runLater().
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            // Clear the current steps from the board group
            for (Node step : stepsNodes) {
                StackPane stepNode = (StackPane) step;
                stepNode.getChildren().clear();
            }

            // Reinitialize the steps based on the board view
            List<Pair<MarkerView, Integer>> playerPositions = boardView.getPlayerPositions();
            int numPlayers = boardView.getNumberOfPlayers();
            int numberOfSteps = stepsNodes.size();

            // Reset the marker in the waiting area - place all player markers at the end positions
            for (int i = 0; i < numPlayers; i++) {
                StackPane stepNode = (StackPane) stepsNodes.get(numberOfSteps-i-1);
                stepNode.getChildren().add(MarkerView.fromValue(i).getNode());
            }

            // Place each player's marker on the corresponding step based on current positions
            for (Pair<MarkerView, Integer> playerPosition : playerPositions) {
                MarkerView marker = playerPosition.getValue0();
                int step = playerPosition.getValue1();

                // Add a marker to the current step position
                StackPane stepNode = (StackPane) stepsNodes.get(step);
                stepNode.getChildren().add(marker.getNode());

                // Remove marker from the waiting area
                ((StackPane) stepsNodes.get(numberOfSteps - marker.getValue() - 1)).getChildren().clear();
            }

            // Update the timer if it exists
            if (boardView.getTimerView() != null && !timerNodes.isEmpty()) {
                // Clear all timer nodes first
                for (Node timerStep : timerNodes) {
                    ((StackPane) timerStep).getChildren().clear();
                }

                // Place timer indicator based on number of flips
                int numberOfFlip = boardView.getTimerView().getNumberOfFlips();
                StackPane stackPane = switch (numberOfFlip) {
                    case 0, 1 -> (StackPane) timerNodes.get(0);
                    case 2 -> (StackPane) timerNodes.get(1);
                    case 3 -> (StackPane) timerNodes.get(2);
                    default -> throw new IllegalStateException("Unexpected value: " + numberOfFlip);
                };
                stackPane.getChildren().add(boardView.getTimerView().getNode().getValue0());
            }

                // Update the decks if they exist
            if (!decksNodes.isEmpty()) {
                DeckView[] decks = boardView.getDecksView().getValue0();
                int i = 0;
                // Iterate through available decks and update their visual representation
                for (DeckView deck : decks) {
                    if (deck != null) {
                        StackPane deckNode = (StackPane) decksNodes.get(i);
                        deckNode.getChildren().clear();
                        deckNode.getChildren().add(deck.getNode().getValue0());
                        i++;
                    }
                }
            }
        });
    }

    /**
     * Returns a list of DeckController instances for each deck in the board view.
     * This method retrieves the DeckController from each DeckView in the board's decks.
     *
     * @return a List of DeckController instances
     */
    public List<DeckController> getDeckControllers() {
        List<DeckController> deckControllers = new ArrayList<>();
        for (DeckView deck : boardView.getDecksView().getValue0()) {
            if (deck != null) {
                Pair<Node, DeckController> pair = deck.getNode();
                if (pair != null) {
                    deckControllers.add(pair.getValue1());
                }
            }
        }

        return deckControllers;
    }
}
