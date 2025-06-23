package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

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

    private double MAIN_HBOX_WIDTH_RATIO;
    private double MAIN_HBOX_HEIGHT_RATIO;
    private double ORIGINAL_MAIN_HBOX_WIDTH;
    private double ORIGINAL_MAIN_HBOX_HEIGHT;

    private double TITLE_IMAGE_WIDTH_RATIO;

    private double RIGHT_VBOX_WIDTH_RATIO;

    /**
     * The ratio of the width of the title image to the width of the main HBox.
     * This is used to maintain the aspect ratio of the title image when resizing.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the background image for the parent StackPane
        URL imageUrl = getClass().getResource("/image/background/background1.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }

        // Set default values for the ratios
        setDefaultValue();

        // Center the main HBox in the parent StackPane
        StackPane.setAlignment(mainHBox, javafx.geometry.Pos.CENTER);

        // Unbind the main HBox properties to allow dynamic resizing
        mainHBox.prefWidthProperty().unbind();
        mainHBox.prefHeightProperty().unbind();
        mainHBox.maxWidthProperty().unbind();
        mainHBox.maxHeightProperty().unbind();
        mainHBox.minWidthProperty().unbind();
        mainHBox.minHeightProperty().unbind();

        // Listener to handle resizing of the parent StackPane
        ChangeListener<Number> resizeListener = (_, _, _) -> {
            double maxWidth = parent.getWidth() * MAIN_HBOX_WIDTH_RATIO;
            double maxHeight = parent.getHeight() * MAIN_HBOX_HEIGHT_RATIO;

            // Computes the scale factor to fit the main HBox within the parent dimensions
            double scaleX = maxWidth / ORIGINAL_MAIN_HBOX_WIDTH;
            double scaleY = maxHeight / ORIGINAL_MAIN_HBOX_HEIGHT;
            double scale = Math.min(scaleX, scaleY);

            // Applies the scale to the main HBox dimensions
            double scaledWidth = ORIGINAL_MAIN_HBOX_WIDTH * scale;
            double scaledHeight = ORIGINAL_MAIN_HBOX_HEIGHT * scale;

            mainHBox.setPrefWidth(scaledWidth);
            mainHBox.setPrefHeight(scaledHeight);
            mainHBox.setMaxWidth(scaledWidth);
            mainHBox.setMaxHeight(scaledHeight);
            mainHBox.setMinWidth(scaledWidth);
            mainHBox.setMinHeight(scaledHeight);

            // Update the sizes of the children components based on the new dimensions
            updateChildrenSizes();
        };

        // Add the resize listener to the parent StackPane
        parent.widthProperty().addListener(resizeListener);
        parent.heightProperty().addListener(resizeListener);

        // Add a listener to the layout bounds of the parent StackPane
        parent.layoutBoundsProperty().addListener((_, _, newBounds) -> {
            if (newBounds.getWidth() > 0 && newBounds.getHeight() > 0) {
                Platform.runLater(() -> resizeListener.changed(null, null, parent.getWidth()));
            }
        });

        // Initial call to set the sizes of the children components
        Platform.runLater(() -> {
            updateChildrenSizes();

            // Trigger the resize listener to set initial sizes
            Platform.runLater(() -> resizeListener.changed(null, null, parent.getWidth()));
        });
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

        updateFontSizes();
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
    }

    private void updateFontSizes() {
        double height = mainHBox.getHeight();
        // The original font size was 72.0 on a height of 900.0
        double lobbyLabelFontSize = Math.max(18, height * 0.08);

        lobbyNameLabel.setFont(Font.font(lobbyNameLabel.getFont().getFamily(), lobbyLabelFontSize));
    }

    @Override
    public void react() {
        MiniModel mm = MiniModel.getInstance();

        // Update the lobbies in the lobby box VBox
        lobbyBoxVBox.getChildren().clear();
        for (LobbyView lv : mm.getLobbiesView()) {
            Node lobbyBoxNode = lv.getNode();
            ((StackPane) lobbyBoxNode).prefWidthProperty().bind(lobbyBoxVBox.widthProperty().subtract(20));
            lobbyBoxVBox.getChildren().add(lobbyBoxNode);
        }
    }
}
