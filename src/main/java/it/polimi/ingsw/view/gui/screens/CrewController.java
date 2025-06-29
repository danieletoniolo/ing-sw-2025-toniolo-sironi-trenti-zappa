package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.clientToServer.spaceship.ManageCrewMember;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.components.ComponentTypeView;
import it.polimi.ingsw.view.miniModel.components.ComponentView;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Crew screen in the GUI.
 * Handles user interactions related to crew management, resizing, and displaying other players' spaceships.
 * Implements MiniModelObserver to react to model changes and Initializable for JavaFX initialization.
 */
public class CrewController implements MiniModelObserver, Initializable {

    /** Root StackPane of the scene, used for layout and overlays. */
    @FXML private StackPane parent;

    /** Group used for resizing the main content. */
    @FXML private Group resizeGroup;

    /** Main VBox containing the primary UI elements. */
    @FXML private VBox mainVBox;

    /** Label displaying the title of the screen. */
    @FXML private Label titleLabel;

    /** StackPane containing the spaceship view. */
    @FXML private StackPane spaceShipStackPane;

    /** HBox containing the lower action buttons. */
    @FXML private HBox lowerHBox;

    /** Button to confirm the end of the turn. */
    private Button endTurnButton;

    /** Overlay pane for crew options. */
    private StackPane newCrewOptionsPane;
    /** VBox containing crew options UI. */
    private VBox newCrewOptionsVBox;

    /** Overlay pane for viewing another player's spaceship. */
    private StackPane newOtherPlayerPane;
    /** VBox containing other player's spaceship UI. */
    private VBox newOtherPlayerVBox;

    /** Original width of the main box for scaling calculations. */
    private final double ORIGINAL_MAIN_BOX_WIDTH = 1600;
    /** Original height of the main box for scaling calculations. */
    private final double ORIGINAL_MAIN_BOX_HEIGHT = 900;

    /** Reference to the MiniModel singleton instance. */
    private final MiniModel mm = MiniModel.getInstance();

    /**
     * Initializes the Crew screen controller.
     * Sets up the background, layout alignment, resize listeners, and action buttons.
     * Called automatically by JavaFX after the FXML fields are injected.
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

        int HBoxTotalButtons = 1 + mm.getOtherPlayers().size();

        endTurnButton = new Button("Confirm spaceship");
        endTurnButton.prefWidthProperty().bind(lowerHBox.widthProperty().divide(HBoxTotalButtons));

        lowerHBox.getChildren().addAll(endTurnButton);
        for (PlayerDataView player : mm.getOtherPlayers()) {
            Button otherButtonPlayer = new Button("View " + player.getUsername() + "'s spaceship");
            otherButtonPlayer.setOnMouseClicked(_ -> showOtherPlayer(player));
            otherButtonPlayer.prefWidthProperty().bind(lowerHBox.widthProperty().divide(HBoxTotalButtons));
            otherButtonPlayer.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
            lowerHBox.getChildren().add(otherButtonPlayer);
        }
        endTurnButton.setOnMouseClicked(_ -> {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object()).request(new EndTurn(mm.getUserID()));
            Platform.runLater(() -> {
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    MessageController.showErrorMessage(((Pota) status).errorMessage());
                } else {
                    MessageController.showInfoMessage("Waiting for other players to end their turn...");
                }
            });
        });
        endTurnButton.setStyle("-fx-background-color: rgba(251,197,9, 0.5); -fx-border-color: rgb(251,197,9); -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-font-weight: bold");
    }

    /**
     * Creates a ChangeListener that rescales the resizeGroup based on the parent StackPane's current width and height.
     * Ensures the UI scales proportionally to the original design dimensions.
     *
     * @return a ChangeListener that updates the scale of the resizeGroup
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
     * Displays an overlay showing another player's spaceship.
     * Creates a modal-like pane with the selected player's ship and a back button to close the overlay.
     *
     * @param player the PlayerDataView representing the player whose spaceship is to be displayed
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
        backButton.setOnAction(_ -> hideOverlay(newOtherPlayerPane));

        newOtherPlayerVBox.getChildren().addAll(titleLabel, otherShip, backButton);
        newOtherPlayerPane.getChildren().add(newOtherPlayerVBox);
        StackPane.setAlignment(newOtherPlayerVBox, Pos.CENTER);

        parent.getChildren().add(newOtherPlayerPane);
        newOtherPlayerPane.setVisible(false);

        Platform.runLater(() -> {

            newOtherPlayerPane.setVisible(true);
            newOtherPlayerPane.toFront(); // Poi il popup va davanti al background

            parent.layout();

            newOtherPlayerPane.setOpacity(0);

            FadeTransition fadeInContent = new FadeTransition(Duration.millis(300), newOtherPlayerPane);
            fadeInContent.setFromValue(0);
            fadeInContent.setToValue(1);

            fadeInContent.play();
        });
    }

    /**
     * Hides the specified overlay pane with a fade-out animation and removes it from the parent StackPane.
     *
     * @param paneToHide the StackPane overlay to hide and remove
     */
    private void hideOverlay(StackPane paneToHide) {
        FadeTransition fadeOutContent = new FadeTransition(Duration.millis(300), paneToHide);
        fadeOutContent.setFromValue(1);
        fadeOutContent.setToValue(0);

        fadeOutContent.setOnFinished(_ -> {
            paneToHide.setVisible(false);
            parent.getChildren().remove(paneToHide);
        });

        fadeOutContent.play();
    }

    /**
     * Reacts to changes in the MiniModel.
     * Updates the UI to reflect the current state of the player's spaceship,
     * highlights interactable components, and sets up event handlers for crew management.
     * This method is called on the JavaFX Application Thread.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            try {
                resetHandlers();

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

                            node.setOnMouseClicked(_ -> showCrewChoice(component));
                        }
                    }
                }

                spaceShipStackPane.getChildren().clear();
                spaceShipStackPane.getChildren().add(mm.getClientPlayer().getShip().getNode().getValue0());
            } catch (Exception e) {}
        });
    }

    /**
     * Shows the crew choice overlay for the specified component.
     * Opens a modal pane allowing the user to select the type of crew member and whether to add or remove them.
     * Triggers a fade-in animation for the overlay.
     *
     * @param component the ComponentView representing the cabin to manage crew for
     */
    private void showCrewChoice(ComponentView component) {
        createNewCrewOptionsPane(component);

        Platform.runLater(() -> {
            newCrewOptionsPane.setVisible(true);
            newCrewOptionsPane.toFront();
            parent.layout();

            newCrewOptionsPane.setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newCrewOptionsPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
    }

    /**
     * Creates and displays a new overlay pane for crew options.
     * This pane allows the user to select the type of crew member and whether to add or remove them
     * for the specified component (cabin). The overlay is styled and sized relative to the main UI,
     * and includes confirm and cancel actions.
     *
     * @param component the ComponentView representing the cabin to manage crew for
     */
    private void createNewCrewOptionsPane(ComponentView component) {
        newCrewOptionsPane = new StackPane();
        newCrewOptionsPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

        StackPane.setAlignment(newCrewOptionsPane, Pos.CENTER);

        newCrewOptionsPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newCrewOptionsPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Create a VBox to hold the new lobby options
        newCrewOptionsVBox = new VBox(15);
        newCrewOptionsVBox.setAlignment(javafx.geometry.Pos.CENTER);
        newCrewOptionsVBox.setStyle("-fx-background-color: rgba(251,197,9, 0.8); " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: rgb(251,197,9); " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 10; " +
                "-fx-padding: 20;");

        // Bind the size of the VBox to the main HBox
        newCrewOptionsVBox.prefWidthProperty().bind(mainVBox.widthProperty().multiply(0.3));
        newCrewOptionsVBox.prefHeightProperty().bind(mainVBox.heightProperty().multiply(0.5));
        newCrewOptionsVBox.minWidthProperty().bind(newCrewOptionsVBox.prefWidthProperty());
        newCrewOptionsVBox.minHeightProperty().bind(newCrewOptionsVBox.prefHeightProperty());
        newCrewOptionsVBox.maxWidthProperty().bind(newCrewOptionsVBox.prefWidthProperty());
        newCrewOptionsVBox.maxHeightProperty().bind(newCrewOptionsVBox.prefHeightProperty());

        // Create a title label with a drop shadow effect
        Label titleLabel = new Label("Select crew type");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setEffect(new DropShadow());


        // ComboBox for type crew selection
        ComboBox<String> crewType = new ComboBox<>();
        crewType.getItems().addAll("Human", "Brown alien", "Purple alien");
        crewType.setValue("Human");
        crewType.setPromptText("Select crew type:");
        crewType.setMaxWidth(newCrewOptionsVBox.getMaxWidth() * 0.8);

        // ComboBox for add or remove of players
        ComboBox<String> modeChose = new ComboBox<>();
        modeChose.getItems().addAll("Add crew member", "Remove crew members");
        modeChose.setValue("Add crew member");
        modeChose.setPromptText("Remove or add crew member:");
        modeChose.setMaxWidth(newCrewOptionsVBox.getMaxWidth() * 0.8);

        // Buttons box to hold the confirm and cancel buttons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        // Create confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnMouseClicked(_ -> {
            String selectedLevel = crewType.getValue();
            int type = selectedLevel.equals("Human") ? 0 : selectedLevel.equals("Brown alien") ? 1 : 2;
            int mode = modeChose.getValue().equals("Add crew member") ? 0 : 1;
            StatusEvent status = ManageCrewMember.requester(Client.transceiver, new Object()).request(new ManageCrewMember(mm.getUserID(), mode, type, component.getID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                MessageController.showErrorMessage(((Pota) status).errorMessage());
            }
        });
        confirmButton.setOnAction(_ -> hideOptions());

        // Create cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(_ -> hideOptions());

        buttonsBox.getChildren().addAll(confirmButton, cancelButton);

        // Add all components to the VBox
        newCrewOptionsVBox.getChildren().addAll(titleLabel,
                new Label("Crew type:"),
                crewType,
                new Label("Number of Players:"),
                modeChose,
                buttonsBox);

        newCrewOptionsPane.getChildren().add(newCrewOptionsVBox);
        StackPane.setAlignment(newCrewOptionsVBox, Pos.CENTER);

        // Add the new lobby options pane to the parent StackPane
        parent.getChildren().add(newCrewOptionsPane);
        newCrewOptionsPane.setVisible(false);

        // Force the layout to update and bring the new pane to the front
        Platform.runLater(() -> {
            newCrewOptionsPane.toFront();
            parent.layout();
        });

        // Update the sizes of the new lobby options controls
        Platform.runLater(() -> {
            newCrewOptionsPane.toFront();
            parent.layout();
            //updateNewLobbyOptionsSizes();
        });
    }

    /**
     * Hides the crew options overlay with a fade-out animation.
     * Sets the overlay as invisible after the animation completes.
     */
    private void hideOptions() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), newCrewOptionsPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> newCrewOptionsPane.setVisible(false));
        fadeOut.play();
    }

    /**
     * Hides the specified crew options overlay pane with a fade-out animation.
     * After the animation, sets the pane as invisible.
     *
     * @param pane the StackPane overlay to hide
     */
    private void hideCrewOptions(StackPane pane) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), pane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> pane.setVisible(false));
        fadeOut.play();
    }

    /**
     * Removes all mouse click handlers and visual effects from every component
     * in the player's spaceship. This is used to reset the UI before reapplying
     * new handlers or effects, ensuring no duplicate or stale listeners remain.
     */
    private void resetHandlers() {
        for (ComponentView[] row : mm.getClientPlayer().getShip().getSpaceShip()) {
            for (ComponentView component : row) {
                if (component != null) {
                    Node node = component.getNode().getValue0();
                    node.setOnMouseClicked(null);
                    node.setEffect(null);
                }
            }
        }
    }
}
