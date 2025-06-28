package it.polimi.ingsw.view.gui.controllers.misc;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.timer.TimerView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the countdown timer in the GUI.
 * Implements the observer pattern to react to changes in the TimerView model
 * and manages the visual representation and countdown logic of the timer.
 */
public class TimerCountdownController implements MiniModelObserver, Initializable {
    /**
     * The StackPane that serves as the parent container for the timer.
     */
    @FXML private StackPane parent;

    /**
     * The Group that contains the timer elements, including the arc and label.
     */
    @FXML private Group timerGroup;

    /**
     * The Arc that represents the progress of the timer.
     * It is used to visually indicate the remaining time.
     */
    @FXML private Arc arcProgress;

    /**
     * The Label that displays the remaining time in seconds.
     * It is updated every second during the countdown.
     */
    @FXML private Label labelTimer;

    /**
     * The Timeline that manages the countdown.
     * It updates the UI every second until the timer reaches zero.
     */
    private Timeline timeline;

    /**
     * The total number of seconds for the countdown.
     * It is set when the timer starts and used to calculate the remaining time.
     */
    private int totalSeconds;

    /**
     * The remaining seconds in the countdown.
     * It is decremented every second until it reaches zero.
     */
    private int remainingSeconds;

    /**
     * The TimerView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    private TimerView timerView;


    /**
     * The original width of the timer group.
     * This is used to calculate the scale factor when resizing.
     */
    private static double ORIGINAL_WIDTH;

    /**
     * The original height of the timer group.
     * This is used to calculate the scale factor when resizing.
     */
    private static double ORIGINAL_HEIGHT;

    /**
     * Initializes the controller by binding the scale and position of the timer group
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the default values for the original width and height
        this.setDefaultValues();

        // Set the group to not be maneged by itself
        timerGroup.setManaged(false);

        // Set the initial posion and scale of the timer group
        updateTimerPositionAndScale();

        // Manual bind the width and height of the timer group to the parent StackPane
        parent.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateTimerPositionAndScale();
        });

        parent.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateTimerPositionAndScale();
        });
    }

    /**
     * Updates the position and scale of the timer group based on the parent StackPane dimensions.
     * This method calculates the scale factor and centers the timer group within the parent.
     */
    private void updateTimerPositionAndScale() {
        double scaleX = parent.getWidth() / ORIGINAL_WIDTH;
        double scaleY = parent.getHeight() / ORIGINAL_HEIGHT;
        double scale = Math.min(scaleX, scaleY);

        timerGroup.setScaleX(scale);
        timerGroup.setScaleY(scale);

        double centerX = (parent.getWidth() - ORIGINAL_WIDTH) / 2;
        double centerY = (parent.getHeight() - ORIGINAL_HEIGHT) / 2;

        timerGroup.setLayoutX(centerX);
        timerGroup.setLayoutY(centerY);
    }

    /**
     * Sets the default values for the original width and height of the timer.
     */
    private void setDefaultValues() {
        ORIGINAL_WIDTH = parent.getPrefWidth();
        ORIGINAL_HEIGHT = parent.getPrefHeight();
    }

    /**
     * Begins a countdown for the specified number of seconds.
     * @param seconds Total duration in seconds.
     */
    private void start(int seconds) {
        parent.setVisible(true);

        if (timeline != null) {
            timeline.stop();
        }

        this.totalSeconds = seconds;
        this.remainingSeconds = seconds;

        this.updateUI();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            updateUI();
            if (remainingSeconds <= 0) {
                timeline.stop();
                onFinished();
            }
        }));

        timeline.setCycleCount(seconds);
        timeline.play();
    }

    /**
     * Updates the arc length and label text.
     */
    private void updateUI() {
        labelTimer.setText(String.valueOf(remainingSeconds));
        double progress = (double)(totalSeconds - remainingSeconds) / totalSeconds;
        arcProgress.setLength(-progress * 360);
    }

    /**
     * Callback when the countdown finishes.
     */
    private void onFinished() {
        // TODO: We could trigger something here or make a nice animation
        labelTimer.setText("!");
        if (timerView.getNumberOfFlips() == 3) {
            parent.setVisible(false);
        }
    }

    public void setModel(TimerView timerView) {
        this.timerView = timerView;
        this.timerView.registerObserver(this);
    }

    @Override
    public void react() {
        Platform.runLater(() -> {
            int time = timerView.getSecondsRemaining();
            this.start(time);
        });
    }

    public Node getParent() {
        return parent;
    }
}