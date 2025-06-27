package it.polimi.ingsw.view.gui.controllers.board;

import it.polimi.ingsw.view.gui.controllers.deck.DeckController;
import it.polimi.ingsw.view.gui.controllers.misc.TimerCountdownController;
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

    // Original dimensions of the background image.
    private double ORIGINAL_WIDTH;
    private double ORIGINAL_HEIGHT;

    // Original dimensions and positions of the background image used for scaling.
    private double ORIGINAL_IMAGE_WIDTH;
    private double ORIGINAL_IMAGE_HEIGHT;
    private double ORIGINAL_IMAGE_X;
    private double ORIGINAL_IMAGE_Y;


    // Lists to store the original positions and dimensions of steps, timers, and decks.

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stepsNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") && Integer.parseInt(node.getId()) >= 0));
        stepsNodes.sort(Comparator.comparingInt(a -> Integer.parseInt(a.getId())));

        timerNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") &&
                        Integer.parseInt(node.getId()) >= -3 && Integer.parseInt(node.getId()) <= -1));
        timerNodes.sort((a, b) -> Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId())));

        decksNodes = new ArrayList<>(boardGroup.getChildren().filtered(node ->
                node instanceof StackPane && node.getId() != null &&
                        node.getId().matches("-?\\d+") &&
                        Integer.parseInt(node.getId()) >= -7 && Integer.parseInt(node.getId()) <= -4));
        decksNodes.sort((a, b) -> Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId())));

        setDefaultValues();

        boardGroup.setManaged(false);

        DoubleBinding scaleFactorBinding = Bindings.createDoubleBinding(() -> {
            double parentWidth = parent.getWidth();
            double parentHeight = parent.getHeight();
            if (parentWidth <= 0 || parentHeight <= 0) return 1.0;
            return Math.min(parentWidth / ORIGINAL_WIDTH, parentHeight / ORIGINAL_HEIGHT);
        }, parent.widthProperty(), parent.heightProperty());

        backgroundImage.layoutXProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_X));
        backgroundImage.layoutYProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_Y));
        backgroundImage.fitWidthProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_WIDTH));
        backgroundImage.fitHeightProperty().bind(scaleFactorBinding.multiply(ORIGINAL_IMAGE_HEIGHT));

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

    private void setDefaultValues() {
        ORIGINAL_WIDTH = boardGroup.getLayoutBounds().getWidth();
        ORIGINAL_HEIGHT = boardGroup.getLayoutBounds().getHeight();

        ORIGINAL_IMAGE_WIDTH = backgroundImage.getFitWidth();
        ORIGINAL_IMAGE_HEIGHT = backgroundImage.getFitHeight();
        ORIGINAL_IMAGE_X = backgroundImage.getLayoutX();
        ORIGINAL_IMAGE_Y = backgroundImage.getLayoutY();

        originalStepX.clear();
        originalStepY.clear();
        originalStepWidth.clear();
        originalStepHeight.clear();

        for (Node step : stepsNodes) {
            originalStepX.add(step.getLayoutX());
            originalStepY.add(step.getLayoutY());
            originalStepWidth.add(((StackPane) step).getPrefWidth());
            originalStepHeight.add(((StackPane) step).getPrefHeight());
        }

        originalTimerX.clear();
        originalTimerY.clear();
        originalTimerWidth.clear();
        originalTimerHeight.clear();

        for (Node timer : timerNodes) {
            originalTimerX.add(timer.getLayoutX());
            originalTimerY.add(timer.getLayoutY());
            originalTimerWidth.add(((StackPane) timer).getPrefWidth());
            originalTimerHeight.add(((StackPane) timer).getPrefHeight());
        }

        originalDeckX.clear();
        originalDeckY.clear();
        originalDeckWidth.clear();
        originalDeckHeight.clear();

        for (Node deck : decksNodes) {
            originalDeckX.add(deck.getLayoutX());
            originalDeckY.add(deck.getLayoutY());
            originalDeckWidth.add(((StackPane) deck).getPrefWidth());
            originalDeckHeight.add(((StackPane) deck).getPrefHeight());
        }
    }

    private void bindStepsElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < stepsNodes.size(); i++) {
            Node step = stepsNodes.get(i);

            step.layoutXProperty().bind(scaleFactorBinding.multiply(originalStepX.get(i)));
            step.layoutYProperty().bind(scaleFactorBinding.multiply(originalStepY.get(i)));
            ((StackPane) step).prefWidthProperty().bind(scaleFactorBinding.multiply(originalStepWidth.get(i)));
            ((StackPane) step).prefHeightProperty().bind(scaleFactorBinding.multiply(originalStepHeight.get(i)));
        }
    }

    private void bindTimerElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < timerNodes.size(); i++) {
            Node timer = timerNodes.get(i);

            timer.layoutXProperty().bind(scaleFactorBinding.multiply(originalTimerX.get(i)));
            timer.layoutYProperty().bind(scaleFactorBinding.multiply(originalTimerY.get(i)));
            ((StackPane) timer).prefWidthProperty().bind(scaleFactorBinding.multiply(originalTimerWidth.get(i)));
            ((StackPane) timer).prefHeightProperty().bind(scaleFactorBinding.multiply(originalTimerHeight.get(i)));
        }
    }

    private void bindDeckElements(DoubleBinding scaleFactorBinding) {
        for (int i = 0; i < decksNodes.size(); i++) {
            Node deck = decksNodes.get(i);

            deck.layoutXProperty().bind(scaleFactorBinding.multiply(originalDeckX.get(i)));
            deck.layoutYProperty().bind(scaleFactorBinding.multiply(originalDeckY.get(i)));
            ((StackPane) deck).prefWidthProperty().bind(scaleFactorBinding.multiply(originalDeckWidth.get(i)));
            ((StackPane) deck).prefHeightProperty().bind(scaleFactorBinding.multiply(originalDeckHeight.get(i)));
        }
    }

    public void setModel(BoardView boardView) {
        this.boardView = boardView;
        this.boardView.registerObserver(this);
        this.react();
    }

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

            // Reset the marker in the waiting area
            for (int i = 0; i < numPlayers; i++) {
                StackPane stepNode = (StackPane) stepsNodes.get(numberOfSteps-i-1);
                stepNode.getChildren().add(MarkerView.fromValue(i).getNode());
            }

            // Place each player's marker on the corresponding step
            for (Pair<MarkerView, Integer> playerPosition : playerPositions) {
                MarkerView marker = playerPosition.getValue0();
                int step = playerPosition.getValue1();

                StackPane stepNode = (StackPane) stepsNodes.get(step);
                stepNode.getChildren().add(marker.getNode());

                ((StackPane) stepsNodes.get(numberOfSteps - marker.getValue() - 1)).getChildren().clear();
            }

            // Update the timer if it exists
            if (boardView.getTimerView() != null && !timerNodes.isEmpty()) {
                for (Node timerStep : timerNodes) {
                    ((StackPane) timerStep).getChildren().clear();
                }

                int numberOfFlip = boardView.getTimerView().getNumberOfFlips();
                StackPane stackPane;
                if (numberOfFlip > 0) {
                    stackPane = (StackPane) timerNodes.get(numberOfFlip - 1);
                } else {
                    stackPane = (StackPane) timerNodes.getFirst();
                }
                stackPane.getChildren().add(boardView.getTimerView().getNode().getValue0());
            }

            if (!decksNodes.isEmpty()) {
                // Update the decks if they exist
                DeckView[] decks = boardView.getDecksView().getValue0();
                int i = 0;
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

    public TimerCountdownController getTimerController() {
        return boardView.getTimerView().getNode().getValue1();
    }

    public List<Node> getStepsNodes() {
        return stepsNodes;
    }
}
