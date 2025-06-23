package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.lobby.LobbyView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
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
     * The ImageView that displays the background image of the lobby.
     * This is used to set the visual theme of the lobby screen.
     */
    @FXML private ImageView backgroundImage;

    /**
     * The SplitPane that divides the lobby screen into different sections.
     * This is used to manage the layout of the lobby elements.
     */
    @FXML private SplitPane mainsplitPane;

    /**
     * The ImageView that displays the title image of the lobby.
     * This is used to show the lobby title visually.
     */
    @FXML private ImageView titleImage;

    /**
     * The SplitPane that is used for vertical layout in the lobby.
     * This is used to manage the layout of the lobby elements vertically.
     */
    @FXML private SplitPane verticalSplitPane;

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
     * This is used to layout the individual player boxes in the lobby.
     */
    @FXML private VBox lobbyBoxVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the background image from resources
        URL imageUrl = getClass().getResource("/image/background/background1.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }


        parent.setMaxWidth(Double.MAX_VALUE);
        parent.setMaxHeight(Double.MAX_VALUE);

        // Binding per l'immagine del titolo
        titleImage.fitHeightProperty().bind(parent.heightProperty().multiply(0.42));// Mantiene proporzione
        titleImage.fitWidthProperty().bind(parent.widthProperty().multiply(0.35));// Mantiene proporzione

        // Binding per lo ScrollPane
        lobbyBoxScrollPane.prefHeightProperty().bind(parent.heightProperty().multiply(0.73));
        lobbyBoxScrollPane.prefWidthProperty().bind(parent.widthProperty().multiply(0.60));

        // Listener per aggiornare le dimensioni dei font quando la finestra viene ridimensionata
        parent.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            updateFontSizes();
        });

        // Imposta il wrap del testo per evitare troncamenti
        lobbyNameLabel.setWrapText(true);
    }

    private void updateFontSizes() {
        double height = parent.getHeight();
        // Il font originale era 72.0 su un'altezza di 900.0
        double lobbyLabelFontSize = Math.max(18, height * 0.08);

        lobbyNameLabel.setFont(Font.font(lobbyNameLabel.getFont().getFamily(), lobbyLabelFontSize));
    }

    @Override
    public void react() {
        MiniModel mm = MiniModel.getInstance();

        // Update the lobbies in the lobby box VBox
        lobbyBoxVBox.getChildren().clear();
        for (LobbyView lv : mm.getLobbiesView()) {
            lobbyBoxVBox.getChildren().add(lv.getNode());
        }
    }
}
