package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.PlayerReady;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class LobbyController implements MiniModelObserver, Initializable {

    /**
     * The parent StackPane that contains the lobby elements.
     * This is used to manage the layout and display of the lobby screen.
     */
    @FXML private StackPane parent;

    /**
     * The SplitPane that contains the main layout of the lobby.
     * This is used to separate the lobby title and the lobby boxes.
     */
    @FXML private HBox mainHBox;

    /**
     * The ImageView that displays the title image of the lobby.
     * This is used to show the lobby title visually.
     */
    @FXML private ImageView titleImage;

    /**
     * The VBox that contains the right side of the lobby.
     * This is used to display the lobby name and other information.
     */
    @FXML private VBox rightVBox;

    /**
     * The Label that displays the name of the lobby.
     * This is used to show the current lobby name to the players.
     */
    @FXML private Label lobbyNameLabel;

    /**
     * The ScrollPane that contains the lobby boxes.
     * This is used to allow scrolling through the list of players in the lobby.
     */
    @FXML private ScrollPane lobbyBoxScrollPane;

    /**
     * The VBox that contains the lobby boxes.
     * This is used to lay out the individual player boxes in the lobby.
     */
    @FXML private VBox lobbyBoxVBox;

    /**
     * The Button that allows players to create a new lobby.
     * This is used to initiate the creation of a new game lobby.
     */
    @FXML private Button readyOrNotButton;

    /**
     * The ratio of the width of the main HBox to the width of the parent StackPane.
     * This is used to maintain the aspect ratio of the main HBox when resizing.
     */
    private double MAIN_HBOX_WIDTH_RATIO;

    /**
     * The ratio of the height of the main HBox to the height of the parent StackPane.
     * This is used to maintain the aspect ratio of the main HBox when resizing.
     */
    private double MAIN_HBOX_HEIGHT_RATIO;

    /**
     * The original width of the main HBox when it was first loaded.
     * This is used to maintain the aspect ratio during resizing.
     */
    private double ORIGINAL_MAIN_HBOX_WIDTH;

    /**
     * The original height of the main HBox when it was first loaded.
     * This is used to maintain the aspect ratio during resizing.
     */
    private double ORIGINAL_MAIN_HBOX_HEIGHT;

    /**
     * The ratio of the width of the title image to the width of the main HBox.
     * This is used to maintain the aspect ratio of the title image when resizing.
     */
    private double TITLE_IMAGE_WIDTH_RATIO;

    /**
     * The ratio of the width of the right VBox to the width of the main HBox.
     * This is used to maintain the aspect ratio of the right VBox when resizing.
     */
    private double RIGHT_VBOX_WIDTH_RATIO;

    /**
     * The ratio of the height of the ready or not button to the height of the main HBox.
     * This is used to maintain the aspect ratio of the button when resizing.
     */
    private double BUTTON_SIZE_RATIO_HEIGHT;

    /**
     * The ratio of the width of the ready or not button to the width of the main HBox.
     * This is used to maintain the aspect ratio of the button when resizing.
     */
    private double BUTTON_SIZE_RATIO_WIDTH;

    /**
     * Initializes the lobby screen and its components.
     * <p>
     * This method is called automatically after the FXML fields are injected.
     * It performs the following actions:
     * <ul>
     *     <li>Sets the background image for the parent {@link StackPane}.</li>
     *     <li>Loads default values for layout ratios and dimensions from the FXML layout.</li>
     *     <li>Centers the main {@link HBox} in the parent {@link StackPane}.</li>
     *     <li>Adds listeners to the parent {@link StackPane} for dynamic resizing of the layout.</li>
     *     <li>Sets up a listener to handle scene and window changes for proper resizing.</li>
     *     <li>Configures the "Ready/Not Ready" button to send the appropriate event and update its label.</li>
     *     <li>Schedules delayed initialization to ensure correct initial sizing.</li>
     * </ul>
     *
     * @param url            The location used to resolve relative paths for the root object, or {@code null} if not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the background image for the parent StackPane
        URL imageUrl = getClass().getResource("/image/background/background1.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }

        // Load default values for ratios and dimensions from the FXML layout
        this.setDefaultValue();

        // Center the main HBox in the parent StackPane
        StackPane.setAlignment(mainHBox, Pos.CENTER);

        // Add listeners to the parent StackPane for dynamic resizing
        ChangeListener<Number> resizeListener = createResizeListener();
        parent.widthProperty().addListener(resizeListener);
        parent.heightProperty().addListener(resizeListener);

        mainHBox.sceneProperty().addListener((_, _, newScene) -> {
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

        readyOrNotButton.setOnAction(_ -> {
            boolean tryingToBeReady = readyOrNotButton.getText().equals("READY");
            StatusEvent status = PlayerReady.requester(Client.transceiver, new Object()).request(new PlayerReady(MiniModel.getInstance().getUserID(), tryingToBeReady));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                Stage currentStage = (Stage) parent.getScene().getWindow();
                MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
            } else {
                if (tryingToBeReady) {
                    readyOrNotButton.setText("NOT READY");
                } else {
                    readyOrNotButton.setText("READY");
                }
            }
        });

        scheduleDelayedInitialization(resizeListener);
    }

    /**
     * Schedules delayed initialization of the resize listener to ensure the parent StackPane is fully loaded.
     * This is necessary to avoid issues with initial dimensions being zero.
     *
     * @param resizeListener The ChangeListener that will be triggered after the delay.
     */
    private void scheduleDelayedInitialization(ChangeListener<Number> resizeListener) {
        for (int delay : new int[]{100, 300, 500, 1000}) {
            PauseTransition pause = new PauseTransition(Duration.millis(delay));
            pause.setOnFinished(_ -> {
                if (parent.getScene() != null && parent.getWidth() > 0) {
                    Platform.runLater(() -> resizeListener.changed(null, null, null));
                }
            });
            pause.play();
        }
    }

    /**
     * Creates a {@link ChangeListener} that handles resizing of the main HBox and its children
     * when the parent StackPane is resized.
     * <p>
     * The listener calculates the appropriate scale factor based on the current size of the parent,
     * maintaining the aspect ratio defined by the original dimensions and ratios.
     * It then updates the preferred, minimum, and maximum sizes of the main HBox accordingly
     * and triggers an update of the child components' sizes.
     *
     * @return a {@link ChangeListener} that can be attached to width and height properties for dynamic resizing
     */
    private ChangeListener<Number> createResizeListener() {
        return (_, _, _) -> {
            if (parent.getWidth() <= 0 || parent.getHeight() <= 0) {
                return;
            }

            double maxWidth = parent.getWidth() * MAIN_HBOX_WIDTH_RATIO;
            double maxHeight = parent.getHeight() * MAIN_HBOX_HEIGHT_RATIO;

            // Compute the scale ratio based on the most restrictive dimension
            double scaleX = maxWidth / ORIGINAL_MAIN_HBOX_WIDTH;
            double scaleY = maxHeight / ORIGINAL_MAIN_HBOX_HEIGHT;
            double scale = Math.min(scaleX, scaleY);

            // Apply the scale ratio to the main HBox
            double scaledWidth = ORIGINAL_MAIN_HBOX_WIDTH * scale;
            double scaledHeight = ORIGINAL_MAIN_HBOX_HEIGHT * scale;

            mainHBox.setPrefWidth(scaledWidth);
            mainHBox.setPrefHeight(scaledHeight);
            mainHBox.setMaxWidth(scaledWidth);
            mainHBox.setMaxHeight(scaledHeight);
            mainHBox.setMinWidth(scaledWidth);
            mainHBox.setMinHeight(scaledHeight);

            // Update the sizes of the children components
            updateChildrenSizes();
        };
    }

    /**
     * Updates the sizes and styles of the main children components of the lobby screen
     * based on the current size of the main HBox.
     * <p>
     * This method dynamically adjusts the spacing, widths, heights, and font sizes
     * of the main layout elements, including the title image, right VBox, lobby box scroll pane,
     * lobby box VBox, and the "Ready/Not Ready" button. It also updates the font size
     * of the lobby name label and, if present, resizes the player boxes.
     */
    private void updateChildrenSizes() {
        double spacing = mainHBox.getWidth() * 0.035;
        mainHBox.setSpacing(spacing);

        titleImage.setFitWidth(mainHBox.getWidth() * TITLE_IMAGE_WIDTH_RATIO);
        titleImage.setPreserveRatio(true);

        rightVBox.setPrefWidth(mainHBox.getWidth() * RIGHT_VBOX_WIDTH_RATIO);
        rightVBox.setMaxWidth(mainHBox.getWidth() * RIGHT_VBOX_WIDTH_RATIO);
        rightVBox.setMinWidth(mainHBox.getWidth() * RIGHT_VBOX_WIDTH_RATIO);
        rightVBox.setPrefHeight(mainHBox.getHeight());

        lobbyBoxScrollPane.setPrefWidth(rightVBox.getWidth() * 0.95);
        lobbyBoxScrollPane.setPrefHeight(rightVBox.getHeight() * 0.75);
        lobbyBoxScrollPane.setMaxWidth(rightVBox.getWidth() * 0.95);
        lobbyBoxScrollPane.setMaxHeight(rightVBox.getHeight() * 0.75);

        lobbyBoxVBox.setPrefWidth(lobbyBoxScrollPane.getWidth());
        lobbyBoxVBox.setMaxWidth(lobbyBoxScrollPane.getWidth());
        lobbyBoxVBox.setMinWidth(lobbyBoxScrollPane.getWidth());

        double buttonHeight = mainHBox.getHeight() * BUTTON_SIZE_RATIO_HEIGHT;
        double buttonWidth = mainHBox.getWidth() * BUTTON_SIZE_RATIO_WIDTH;

        readyOrNotButton.setPrefSize(buttonWidth, buttonHeight);
        readyOrNotButton.setMinSize(buttonWidth, buttonHeight);
        readyOrNotButton.setMaxSize(buttonWidth, buttonHeight);
        readyOrNotButton.setStyle("-fx-font-size: " + (buttonHeight * 0.4) + "px;" +
                "-fx-background-color: rgba(251,197,9, 0.5);;" +
                "-fx-border-color: rgb(251,197,9);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-weight: bold;");

        // Update the title label font size based on the height of the main HBox
        double height = mainHBox.getHeight();
        double lobbyLabelFontSize = Math.max(18, height * 0.08);
        lobbyNameLabel.setFont(Font.font(lobbyNameLabel.getFont().getFamily(), FontWeight.BOLD, lobbyLabelFontSize));

        // Update the lobby box VBox if it exists and has children
        if (lobbyBoxVBox != null && !lobbyBoxVBox.getChildren().isEmpty()) {
            updatePlayerBoxesSizes();
        }
    }


    /**
     * Updates the size, spacing, padding, and font of each player box in the lobby.
     * <p>
     * This method iterates through all children of the lobbyBoxVBox, and for each child that is an HBox
     * (representing a player box), it sets the spacing, padding, and preferred height based on the current
     * size of the mainHBox. It also updates the font size and style of any Label nodes inside the player box.
     * This ensures that the player boxes scale responsively with the lobby window.
     */
    private void updatePlayerBoxesSizes() {
        double HBoxHeight = mainHBox.getHeight() * 0.1;
        double fontSize = Math.max(1, mainHBox.getHeight() * 0.02);
        double spacing = mainHBox.getWidth() * 0.01;
        double padding = mainHBox.getHeight() * 0.01;

        for (int i = 0; i < lobbyBoxVBox.getChildren().size(); i++) {
            if (lobbyBoxVBox.getChildren().get(i) instanceof HBox playerBox) {
                playerBox.setSpacing(spacing);
                playerBox.setPadding(new javafx.geometry.Insets(padding));

                playerBox.setPrefHeight(HBoxHeight);

                for (javafx.scene.Node node : playerBox.getChildren()) {
                    if (node instanceof Label label) {
                        label.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold; -fx-text-fill: black;");
                    }
                }
            }
        }
    }

    /**
     * Loads and sets the default values for layout ratios and dimensions.
     * <p>
     * This method initializes the ratios and original sizes used for responsive resizing
     * of the lobby screen components. It reads the preferred sizes from the FXML-injected
     * nodes and calculates the ratios for the main HBox, title image, right VBox, and
     * the "Ready/Not Ready" button. These values are used to maintain the correct aspect
     * ratios and scaling behavior when the window is resized.
     */
    private void setDefaultValue() {
        MAIN_HBOX_HEIGHT_RATIO = mainHBox.getPrefHeight() / parent.getPrefHeight();
        MAIN_HBOX_WIDTH_RATIO = mainHBox.getPrefWidth() / parent.getPrefWidth();

        ORIGINAL_MAIN_HBOX_WIDTH = mainHBox.getPrefWidth();
        ORIGINAL_MAIN_HBOX_HEIGHT = mainHBox.getPrefHeight();

        TITLE_IMAGE_WIDTH_RATIO = titleImage.getFitWidth() / mainHBox.getPrefWidth();
        RIGHT_VBOX_WIDTH_RATIO = rightVBox.getPrefWidth() / mainHBox.getPrefWidth();

        BUTTON_SIZE_RATIO_HEIGHT = readyOrNotButton.getPrefHeight() / mainHBox.getPrefHeight();
        BUTTON_SIZE_RATIO_WIDTH = readyOrNotButton.getPrefWidth() / mainHBox.getPrefWidth();
    }

    /**
     * Displays a countdown overlay when the game is about to start.
     * <p>
     * This method creates a semi-transparent overlay with a countdown label,
     * which counts down from 10 seconds to "GO!". The overlay covers the entire
     * parent StackPane and dynamically adjusts its font size based on the main HBox height.
     * After the countdown, the overlay is removed and the resize listener is detached
     * to prevent memory leaks.
     */
    private void gameStarting() {
        // Show a countdown overlay when the game is starting
        Label countdownLabel = new Label("The game will start in\n10");
        countdownLabel.setAlignment(Pos.CENTER);
        countdownLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Function to update the font size
        Runnable updateFontSize = () -> {
            double fontSize = mainHBox.getHeight() * 0.06;
            countdownLabel.setStyle("-fx-font-size: " + fontSize + "px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: #f2ff00; " +
                    "-fx-text-alignment: center;");
        };

        // Set the initial font size
        updateFontSize.run();

        // Create a semi-transparent overlay that covers the entire parent
        StackPane overlay = new StackPane();
        overlay.prefWidthProperty().bind(parent.widthProperty());
        overlay.prefHeightProperty().bind(parent.heightProperty());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.getChildren().add(countdownLabel);

        // Add the overlay to the scene
        parent.getChildren().add(overlay);

        // Add listener for resizing
        ChangeListener<Number> heightListener = (_, _, _) -> updateFontSize.run();
        mainHBox.heightProperty().addListener(heightListener);

        // Create the countdown
        Timeline timeline = new javafx.animation.Timeline();
        Duration duration = javafx.util.Duration.seconds(1);

        // Atomic integer for seconds left
        java.util.concurrent.atomic.AtomicInteger secondsLeft = new java.util.concurrent.atomic.AtomicInteger(10);

        timeline.getKeyFrames().add(
                new javafx.animation.KeyFrame(duration, _ -> {
                    secondsLeft.decrementAndGet();
                    if (secondsLeft.get() > 0) {
                        countdownLabel.setText("The game will start in\n" + secondsLeft.get());
                    } else {
                        countdownLabel.setText("GO!");
                    }
                })
        );

        timeline.setCycleCount(10);
        timeline.setOnFinished(_ -> {
            // Wait an extra second to show "GO!" before removing
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(_ -> {
                parent.getChildren().remove(overlay);
                // Remove the listener to avoid memory leaks
                mainHBox.heightProperty().removeListener(heightListener);
            });
            pause.play();
        });

        timeline.play();
    }


    /**
     * Reacts to updates in the mini model and refreshes the lobby screen accordingly.
     * <p>
     * This method is called when the mini model notifies its observers of a change.
     * It updates the lobby name, clears and repopulates the player list, and adjusts the player box sizes.
     * If the lobby is full (all players are present), it triggers a countdown overlay indicating the game is about to start.
     * All UI updates are performed on the JavaFX Application Thread.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            MiniModel mm = MiniModel.getInstance();

            LobbyView lobbyView = mm.getCurrentLobby();
            if (lobbyView == null) return;

            // If the game is starting, show the countdown
            if (lobbyView.getPlayers().entrySet().stream().filter(Map.Entry::getValue).count() == lobbyView.getPlayers().size()) {
                gameStarting();
            }

            lobbyNameLabel.setText(lobbyView.getLobbyName());
            lobbyBoxVBox.getChildren().clear();
            Map<String, Boolean> players = lobbyView.getPlayers();

            for (Map.Entry<String, Boolean> entry : players.entrySet()) {
                try {
                    MarkerView mv;
                    if (entry.getKey().equals(mm.getNickname())) {
                        mv = mm.getClientPlayer().getMarkerView();
                    } else {
                        mv = mm.getOtherPlayers().stream()
                                .filter(player -> player.getUsername().equals(entry.getKey()))
                                .findFirst()
                                .map(PlayerDataView::getMarkerView)
                                .orElse(null);
                    }

                    // Create a new HBox for the player
                    HBox playerBox = new HBox(10);
                    playerBox.setAlignment(Pos.CENTER_LEFT);
                    playerBox.setSpacing(10);
                    playerBox.setStyle("-fx-background-color: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-text-fill: black; ");

                    // Create a Label for the player's name and status
                    Label playerNameLabel = new Label(entry.getKey() + ": " + (entry.getValue() ? "READY" : "NOT READY"));
                    playerNameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                    playerNameLabel.setStyle("-fx-text-fill: black;");

                    // Add the player's marker view and name label to the player box
                    if (mv != null) {
                        playerBox.getChildren().add(mv.getNode());
                    }
                    playerBox.getChildren().add(playerNameLabel);

                    // Bind the width of the player box to the lobby box VBox width
                    playerBox.prefWidthProperty().bind(lobbyBoxVBox.widthProperty().subtract(20));

                    lobbyBoxVBox.getChildren().add(playerBox);

                } catch (Exception e) {
                    // TODO: Decide whether to log this error or handle it differently
                    System.err.println("Error while loading data of player: " + entry.getKey() + ": " + e.getMessage());
                }
            }

            updatePlayerBoxesSizes();
        });
    }
}
