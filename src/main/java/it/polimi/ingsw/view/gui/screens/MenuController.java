package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.CreateLobby;
import it.polimi.ingsw.event.lobby.clientToServer.JoinLobby;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu screen in the GUI.
 * <p>
 * This class manages the layout, resizing, and user interactions
 * for the lobby menu, including creating and joining lobbies.
 * It observes changes in the MiniModel to update the lobby view dynamically.
 * </p>
 */
public class MenuController implements MiniModelObserver, Initializable {

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
    @FXML private Button createLobbyButton;

    /**
     * The StackPane that it is used as an overlay when the create new lobby options are displayed.
     */
    private StackPane newLobbyOptionsPane;

    /**
     * The VBox that contains the new lobby options.
     * This is used to lay out the controls for creating a new lobby.
     */
    private VBox newLobbyOptionsVBox;

    private double MAIN_HBOX_WIDTH_RATIO;
    private double MAIN_HBOX_HEIGHT_RATIO;
    private double ORIGINAL_MAIN_HBOX_WIDTH;
    private double ORIGINAL_MAIN_HBOX_HEIGHT;

    private double TITLE_IMAGE_WIDTH_RATIO;

    private double RIGHT_VBOX_WIDTH_RATIO;

    private double BUTTON_SIZE_RATIO_HEIGHT;
    private double BUTTON_SIZE_RATIO_WIDTH;

    /**
     * The ratio of the width of the title image to the width of the main HBox.
     * This is used to maintain the aspect ratio of the title image when resizing.
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

        createLobbyButton.setOnAction(_ -> showNewLobbyOptions());

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
     * Creates a {@link ChangeListener} that listens for changes in the width and height of the parent StackPane.
     * This listener is responsible for resizing the main {@link HBox} and its children components based on the
     * parent dimensions.
     *
     * @return A ChangeListener that handles resizing of the main HBox and its children.
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
     * Updates the sizes of the children components based on the main HBox dimensions.
     * This method is called whenever the main HBox is resized to maintain the layout proportions.
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

        createLobbyButton.setPrefSize(buttonWidth, buttonHeight);
        createLobbyButton.setMinSize(buttonWidth, buttonHeight);
        createLobbyButton.setMaxSize(buttonWidth, buttonHeight);
        createLobbyButton.setStyle("-fx-font-size: " + (buttonHeight * 0.4) + "px;" +
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

        // Update the new lobby options sizes if they are currently visible
        if (newLobbyOptionsVBox != null && newLobbyOptionsPane.isVisible()) {
            updateNewLobbyOptionsSizes();
        }
    }

    /**
     * Sets the default values for the ratios and original dimensions of the main HBox.
     */
    private void setDefaultValue() {
        MAIN_HBOX_HEIGHT_RATIO = mainHBox.getPrefHeight() / parent.getPrefHeight();
        MAIN_HBOX_WIDTH_RATIO = mainHBox.getPrefWidth() / parent.getPrefWidth();

        ORIGINAL_MAIN_HBOX_WIDTH = mainHBox.getPrefWidth();
        ORIGINAL_MAIN_HBOX_HEIGHT = mainHBox.getPrefHeight();

        TITLE_IMAGE_WIDTH_RATIO = titleImage.getFitWidth() / mainHBox.getPrefWidth();
        RIGHT_VBOX_WIDTH_RATIO = rightVBox.getPrefWidth() / mainHBox.getPrefWidth();

        BUTTON_SIZE_RATIO_HEIGHT = createLobbyButton.getPrefHeight() / mainHBox.getPrefHeight();
        BUTTON_SIZE_RATIO_WIDTH = createLobbyButton.getPrefWidth() / mainHBox.getPrefWidth();
    }

    /**
     * Reacts to changes in the MiniModel, specifically updates the lobby boxes.
     * This method is called whenever the MiniModel notifies observers of changes,
     * such as when a new lobby is created or an existing lobby is updated.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            try {
                MiniModel mm = MiniModel.getInstance();

                // Update the lobbies in the lobby box VBox
                lobbyBoxVBox.getChildren().clear();
                for (LobbyView lv : mm.getLobbiesView()) {
                    Node lobbyBoxNode = lv.getNode();
                    // Bind the width of the lobby box to the VBox width minus some padding
                    if (lobbyBoxNode instanceof StackPane sp) {
                        sp.prefHeightProperty().bind(lobbyBoxVBox.heightProperty().multiply(0.15));
                        sp.prefWidthProperty().bind(lobbyBoxVBox.widthProperty().subtract(20));
                    }
                    lobbyBoxVBox.getChildren().add(lobbyBoxNode);
                    lobbyBoxVBox.setOnMouseClicked(
                            _ -> {
                                StatusEvent status = JoinLobby.requester(Client.transceiver, new Object())
                                        .request(new JoinLobby(MiniModel.getInstance().getUserID(), lv.getLobbyName()));
                                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                                    MessageController.showErrorMessage(((Pota) status).errorMessage());
                                }
                            }
                    );
                }
            } catch (Exception e) {}
        });
    }

    /**
     * Shows the options for creating a new lobby.
     * This method creates and displays a pane with options for creating a new lobby,
     * including level selection and number of players.
     */
    private void showNewLobbyOptions() {
        if (newLobbyOptionsPane == null) {
            createNewLobbyOptionsPane();
        }

        Platform.runLater(() -> {
            newLobbyOptionsPane.setVisible(true);
            newLobbyOptionsPane.toFront();
            parent.layout();

            newLobbyOptionsPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newLobbyOptionsPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    /**
     * Creates the pane for new lobby options.
     * This method initializes the pane with controls for creating a new lobby,
     * including level selection, number of players, and buttons for confirmation and cancellation.
     */
    private void createNewLobbyOptionsPane() {
        newLobbyOptionsPane = new StackPane();
        newLobbyOptionsPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newLobbyOptionsPane, Pos.CENTER);

        newLobbyOptionsPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newLobbyOptionsPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        newLobbyOptionsVBox = new VBox(15);
        newLobbyOptionsVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newLobbyOptionsVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newLobbyOptionsVBox.prefWidthProperty().bind(mainHBox.widthProperty().multiply(0.3));
        newLobbyOptionsVBox.prefHeightProperty().bind(mainHBox.heightProperty().multiply(0.5));
        newLobbyOptionsVBox.minWidthProperty().bind(newLobbyOptionsVBox.prefWidthProperty());
        newLobbyOptionsVBox.minHeightProperty().bind(newLobbyOptionsVBox.prefHeightProperty());
        newLobbyOptionsVBox.maxWidthProperty().bind(newLobbyOptionsVBox.prefWidthProperty());
        newLobbyOptionsVBox.maxHeightProperty().bind(newLobbyOptionsVBox.prefHeightProperty());


        // Create a title label with a drop shadow effect
        Label titleLabel = new Label("Create New Lobby");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());


        // ComboBox for level selection
        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.getItems().addAll("Learning", "Second");
        levelCombo.setValue("Learning");
        levelCombo.setPromptText("Select Level");
        levelCombo.setMaxWidth(newLobbyOptionsVBox.getMaxWidth() * 0.8);

        // ComboBox for number of players
        ComboBox<Integer> playersCombo = new ComboBox<>();
        playersCombo.getItems().addAll(2, 3, 4);
        playersCombo.setValue(4);
        playersCombo.setPromptText("Number of Players");
        playersCombo.setMaxWidth(newLobbyOptionsVBox.getMaxWidth() * 0.8);

        // Buttons box to hold the confirm and cancel buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create confirm button
        Button confirmButton = new Button("Create");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnAction(_ -> {
            String selectedLevel = levelCombo.getValue();
            int level = selectedLevel.equals("Learning") ? 1 : 2;
            Integer selectedPlayers = playersCombo.getValue();
            StatusEvent status = CreateLobby.requester(Client.transceiver, new Object()).request(new CreateLobby(MiniModel.getInstance().getUserID(), selectedPlayers, level));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                MessageController.showErrorMessage(((Pota) status).errorMessage());
            }
        });

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideNewLobbyOptions());

        buttonsBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all components to the VBox
        newLobbyOptionsVBox.getChildren().addAll(titleLabel,
                new Label("Level:"),
                levelCombo,
                new Label("Number of Players:"),
                playersCombo,
                buttonsBox);

        newLobbyOptionsPane.getChildren().add(newLobbyOptionsVBox);
        StackPane.setAlignment(newLobbyOptionsVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newLobbyOptionsPane);
        newLobbyOptionsPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newLobbyOptionsPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newLobbyOptionsPane.toFront();
            parent.layout();
            updateNewLobbyOptionsSizes();
        });
    }

    /**
     * Updates the sizes of the new lobby options controls based on the main HBox dimensions.
     * This method is called whenever the main HBox is resized to maintain the layout proportions
     * of the new lobby options pane.
     */
    private void updateNewLobbyOptionsSizes() {
        if (newLobbyOptionsVBox == null) return;

        // Compute sizes based on the main HBox dimensions
        double fontSize = Math.max(6, mainHBox.getHeight() * 0.025);
        double titleFontSize = Math.max(9, mainHBox.getHeight() * 0.04);
        double buttonHeight = mainHBox.getHeight() * 0.05;
        double spacing = mainHBox.getHeight() * 0.02;

        // Update the VBox spacing
        newLobbyOptionsVBox.setSpacing(spacing);

        // Update the title label dimensions and style
        Label titleLabel = (Label) newLobbyOptionsVBox.getChildren().getFirst();
        titleLabel.setStyle("-fx-font-size: " + titleFontSize + "px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Update the ComboBoxes and Labels
        for (Node node : newLobbyOptionsVBox.getChildren()) {
            if (node instanceof Label && node != titleLabel) {
                node.setStyle("-fx-font-size: " + fontSize + "px; -fx-text-fill: white;");
            } else if (node instanceof ComboBox<?> comboBox) {
                comboBox.setStyle("-fx-font-size: " + fontSize + "px;");
                comboBox.setMaxWidth(newLobbyOptionsVBox.getPrefWidth() * 0.8);
                comboBox.setPrefHeight(buttonHeight);
            }
        }

        // Update the buttons in the button box
        HBox buttonsBox = (HBox) newLobbyOptionsVBox.getChildren().getLast();
        buttonsBox.setSpacing(spacing);

        for (Node node : buttonsBox.getChildren()) {
            if (node instanceof Button button) {
                button.setPrefHeight(buttonHeight);
                button.setPrefWidth(newLobbyOptionsVBox.getPrefWidth() * 0.3);

                String currentStyle = button.getStyle();
                String newStyle = currentStyle.replaceAll("-fx-font-size: [^;]*;", "") +
                        "; -fx-font-size: " + fontSize + "px;";
                button.setStyle(newStyle);
            }
        }
    }

    /**
     * Hides the new lobby options pane with a fade-out transition.
     * This method is called when the user cancels the creation of a new lobby.
     */
    private void hideNewLobbyOptions() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), newLobbyOptionsPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> newLobbyOptionsPane.setVisible(false));
        fadeOut.play();
    }
}
