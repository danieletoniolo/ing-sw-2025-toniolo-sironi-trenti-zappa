package it.polimi.ingsw.view.gui.controllers.board;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.board.BoardView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class BoardController implements MiniModelObserver {

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
    private List<Node> steps;

    /**
     * A list of Node objects representing timer steps.
     * This is used to display the timer steps on the board.
     */
    private List<Node> timerSteps;

    /**
     * The BoardView model that this controller observes.
     * It provides the current state of the board, including player positions and steps.
     */
    private BoardView boardView;

    // Original dimensions of the background image.
    private double ORIGINAL_IMAGE_WIDTH;
    private double ORIGINAL_IMAGE_HEIGHT;

    @FXML
    private void initialize() {
        setDefaultValues();

        boardGroup.scaleXProperty().unbind();
        boardGroup.scaleYProperty().unbind();
        boardGroup.translateXProperty().unbind();
        boardGroup.translateYProperty().unbind();

        // Create a binding to calculate the scale factor based on the parent StackPane dimensions
        DoubleBinding scaleFactorBinding = Bindings.createDoubleBinding(() -> {
            double parentWidth = parent.getWidth();
            double parentHeight = parent.getHeight();

            if (parentWidth <= 0 || parentHeight <= 0) return 1.0;

            double scaleX = parentWidth / ORIGINAL_IMAGE_WIDTH;
            double scaleY = parentHeight / ORIGINAL_IMAGE_HEIGHT;

            return Math.min(scaleX, scaleY);
        }, parent.widthProperty(), parent.heightProperty());

        // Bind the scale properties of the board group to the scale factor
        boardGroup.scaleXProperty().bind(scaleFactorBinding);
        boardGroup.scaleYProperty().bind(scaleFactorBinding);

        // Center the board group in the parent StackPane
        DoubleBinding centerXBinding = Bindings.createDoubleBinding(() -> {
            double scaledWidth = scaleFactorBinding.get() * ORIGINAL_IMAGE_WIDTH;
            return (parent.getWidth() - scaledWidth) / 2.0;
        }, parent.widthProperty(), scaleFactorBinding);

        DoubleBinding centerYBinding = Bindings.createDoubleBinding(() -> {
            double scaledHeight = scaleFactorBinding.get() * ORIGINAL_IMAGE_HEIGHT;
            return (parent.getHeight() - scaledHeight) / 2.0;
        }, parent.heightProperty(), scaleFactorBinding);

        // Bind the translation properties of the board group to center it
        boardGroup.translateXProperty().bind(centerXBinding);
        boardGroup.translateYProperty().bind(centerYBinding);

        // Save the steps in a list and sort them based on their IDs
        steps = new ArrayList<>(boardGroup.getChildren().filtered(node -> node instanceof StackPane && Integer.parseInt(node.getId()) >= 0));
        steps.sort((a, b) -> {
            if (a != null && b != null) {
                return Integer.compare(Integer.parseInt(a.getId()), Integer.parseInt(b.getId()));
            }
            return 0; // Default case if not both are StackPane
        });

        // The last three children are timer steps, so we filter them out
        timerSteps = new ArrayList<>(boardGroup.getChildren().filtered(node -> node instanceof StackPane && Integer.parseInt(node.getId()) < 0));
        timerSteps.sort((a, b) -> {
            if (a != null && b != null) {
                return Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId()));
            }
            return 0; // Default case if not both are StackPane
        });
    }

    private void setDefaultValues() {
        ORIGINAL_IMAGE_WIDTH = backgroundImage.getFitWidth();
        ORIGINAL_IMAGE_HEIGHT = backgroundImage.getFitHeight();
    }

    public void setModel(BoardView boardView) {
        this.boardView = boardView;
        this.boardView.registerObserver(this);
        this.react();
    }

    @Override
    public void react() {
        // Clear the current steps from the board group
        for (Node step : steps) {
            StackPane stepNode = (StackPane) step;
            stepNode.getChildren().clear();
        }

        // Reinitialize the steps based on the board view
        List<Pair< MarkerView, Integer>> playerPositions = boardView.getPlayerPositions();
        int numPlayers = boardView.getNumberOfPlayers();
        int numberOfSteps = steps.size();

        // Reset the marker in the waiting area
        for (int i = 0; i < numPlayers; i++) {
            StackPane stepNode = (StackPane) steps.get(numberOfSteps-i-1);
            stepNode.getChildren().add(MarkerView.fromValue(i).getNode());
        }

        // Place each player's marker on the corresponding step
        for (Pair<MarkerView, Integer> playerPosition : playerPositions) {
            MarkerView marker = playerPosition.getValue0();
            int step = playerPosition.getValue1();

            StackPane stepNode = (StackPane) steps.get(step);
            stepNode.getChildren().add(marker.getNode());

            ((StackPane) steps.get(numberOfSteps - marker.getValue() - 1)).getChildren().clear();
        }

        // Update the timer if it exists
        if (boardView.getTimerView() != null) {
            for (Node timerStep : timerSteps) {
                ((StackPane) timerStep).getChildren().clear();
            }

            int numberOfFlip = boardView.getTimerView().getNumberOfFlips();
            StackPane stackPane;
            if (numberOfFlip > 0) {
                stackPane = (StackPane) timerSteps.get(numberOfFlip - 1);
            } else {
                stackPane = (StackPane) timerSteps.getFirst();
            }
            stackPane.getChildren().add(boardView.getTimerView().getNode());
        }
    }
}
