package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.clientToServer.player.EndTurn;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.player.MarkerView;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RewardController implements MiniModelObserver, Initializable{

    /**
     * The root pane of the screen, used for resizing and positioning elements.
     */
    @FXML private StackPane parent;

    /**
     * The group that contains the resize handles for the screen.
     */
    @FXML private Group resizeGroup;

    /**
     * The main box that contains the player data boxes.
     */
    @FXML private StackPane mainBox;

    /**
     * The VBox that contains the player data boxes.
     */
    @FXML private VBox rankVBox;

    /**
     * The label that displays the title of the screen.
     */
    @FXML private Label titleLabel;

    /**
     * The button that allows the user to go to the next rank.
     */
    @FXML private Button nextRankButton;

    /**
     * The button that allows the user to go back to the previous rank.
     */
    private final ArrayList<PlayerDataView> allPlayers = new ArrayList<>();

    /**
     * The original width of the main VBox, used for scaling.
     */
    private final double ORIGINAL_MAIN_BOX_WIDTH = 1600;

    /**
     * The original height of the main box, used for scaling.
     */
    private final double ORIGINAL_MAIN_BOX_HEIGHT = 900;

    private final static String[] rankings = {
        "1st Place: ",
        "2nd Place: ",
        "3rd Place: ",
        "4th Place: ",
    };


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

        StackPane.setAlignment(mainBox, Pos.CENTER);

        ChangeListener<Number> resizeListener = createResizeListener();
        parent.widthProperty().addListener(resizeListener);
        parent.heightProperty().addListener(resizeListener);

        mainBox.sceneProperty().addListener((_, _, newScene) -> {
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


        allPlayers.add(MiniModel.getInstance().getClientPlayer());
        allPlayers.addAll(MiniModel.getInstance().getOtherPlayers());
        allPlayers.sort((p1, p2) -> Integer.compare(p2.getCoins(), p1.getCoins()));

        nextRankButton.setText("Next ranking");
        nextRankButton.setOnAction(_ -> {
            StatusEvent status = EndTurn.requester(Client.transceiver, new Object())
                    .request(new EndTurn(MiniModel.getInstance().getUserID()));
            if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                MessageController.showErrorMessage(((Pota) status).errorMessage());
            }
        });
    }

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

    @Override
    public void react() {
        int i = 0;
        for (PlayerDataView player : allPlayers) {
            MarkerView mv = player.getMarkerView();

            // Create a new HBox for the player
            HBox playerBox = new HBox(10);
            playerBox.setAlignment(Pos.CENTER_LEFT);
            playerBox.setSpacing(10);
            playerBox.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 10; " +
                    "-fx-text-fill: black; ");

            // Create a Label for the player's name and status
            Label playerNameLabel = new Label(rankings[i] + player.getUsername() + " with " + player.getCoins() + " coins");
            playerNameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
            playerNameLabel.setStyle("-fx-text-fill: black;");

            // Add the player's marker view and name label to the player box
            if (mv != null) {
                playerBox.getChildren().add(mv.getNode());
            }
            playerBox.getChildren().add(playerNameLabel);

            // Bind the width of the player box to the lobby box VBox width
            playerBox.prefWidthProperty().bind(rankVBox.widthProperty().subtract(20));

            playerBox.prefHeight(100);
            rankVBox.getChildren().add(playerBox);
            i++;
        }
    }
}
