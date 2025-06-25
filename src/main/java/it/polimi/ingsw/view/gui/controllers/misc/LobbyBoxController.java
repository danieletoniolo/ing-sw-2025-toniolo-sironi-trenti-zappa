package it.polimi.ingsw.view.gui.controllers.misc;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyBoxController implements MiniModelObserver, Initializable {
    /**
     * The parent StackPane that contains the lobby box.
     * This is defined in the FXML file and is used to lay out the lobby box components.
     */
    @FXML private StackPane parent;

    /**
     * The HBox that contains the lobby box elements.
     * This is defined in the FXML file and is used to lay. Out the lobby box components.
     */
    @FXML private HBox hBox;

    /**
     * The VBox that contains the right side of the lobby box.
     * This is defined in the FXML file and is used to lay out the lobby box components.
     */
    @FXML private VBox rightVBox;

    /**
     * The Label that displays the name of the lobby.
     * This is defined in the FXML file and is used to show the lobby name.
     */
    @FXML private Label lobbyName;

    /**
     * The Label that displays the level of the lobby.
     * This is defined in the FXML file and is used to show the lobby level.
     */
    @FXML private Label lobbyLevel;

    /**
     * The VBox that contains the left side of the lobby box.
     * This is defined in the FXML file and is used to lay out the lobby box components.
     */
    @FXML private VBox leftVBox;

    /**
     * The Label that displays the maximum number of players allowed in the lobby.
     * This is defined in the FXML file and is used to show the maximum player count.
     */
    @FXML private Label numberOfPlayer;

    /**
     * The LobbyView model that this controller observes.
     * It provides the current state of the lobby, including lobby name, level, and player count.
     */
    private LobbyView lobbyView;

    /**
     * Initializes the LobbyBoxController by setting up the layout and bindings.
     * This method is called automatically when the FXML file is loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object or null if no localization is needed.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ensures that the parent StackPane can grow to fill available space
        parent.setMaxWidth(Double.MAX_VALUE);
        parent.setMaxHeight(Double.MAX_VALUE);

        // Binds the HBox to the parent StackPane dimensions
        hBox.prefWidthProperty().bind(parent.widthProperty());
        hBox.prefHeightProperty().bind(parent.heightProperty());

        // Binds the right and left VBox to the HBox dimensions to maintain their proportions
        rightVBox.prefWidthProperty().bind(hBox.widthProperty().multiply(0.8));
        leftVBox.prefWidthProperty().bind(hBox.widthProperty().multiply(0.2));

        // Enables text wrapping for labels to handle long text gracefully
        lobbyName.setWrapText(true);
        lobbyLevel.setWrapText(true);

        // Adds listeners to the parent StackPane to update font sizes dynamically
        parent.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            updateFontSizes();
        });
    }

    /**
     * Updates the font sizes of the labels based on the height of the parent StackPane.
     */
    private void updateFontSizes() {
        double height = parent.getHeight();
        double lobbyNameSize = Math.max(16, height * 0.3);
        double lobbyLevelSize = Math.max(12, height * 0.2);
        double numberOfPlayerSize = Math.max(16, height * 0.3);

        lobbyName.setFont(Font.font(lobbyName.getFont().getFamily(), lobbyNameSize));
        lobbyLevel.setFont(Font.font(lobbyLevel.getFont().getFamily(), lobbyLevelSize));
        numberOfPlayer.setFont(Font.font(numberOfPlayer.getFont().getFamily(), numberOfPlayerSize));
    }

    /**
     * Sets the LobbyView model for this controller.
     * This method registers the controller as an observer of the LobbyView model
     * and triggers an initial update to reflect the current state of the lobby.
     *
     * @param lobbyView The LobbyView model to be set for this controller.
     */
    public void setModel(LobbyView lobbyView) {
        this.lobbyView = lobbyView;
        this.lobbyView.registerObserver(this);

        this.react();
    }

    /**
     * Updates the UI components to reflect the current state of the lobby.
     * This method is called when the LobbyView model notifies observers of changes.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            if (lobbyView != null) {
                lobbyName.setText(lobbyView.getLobbyName());
                lobbyLevel.setText(lobbyView.getLevel().toString());
                numberOfPlayer.setText(lobbyView.getNumberOfPlayers() + "/" + lobbyView.getMaxPlayer());
            }
        });
    }
}
