package it.polimi.ingsw.view.gui.screens;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.lobby.clientToServer.SetNickname;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.view.gui.controllers.misc.MessageController;
import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements MiniModelObserver, Initializable {

    /**
     * The StackPane that serves as the parent container for the login screen.
     */
    @FXML private StackPane parent;

    /**
     * The VBox that contains the login box elements.
     */
    @FXML private VBox loginBox;

    /**
     * The ImageView that displays the title image of the login screen.
     */
    @FXML private ImageView titleImage;

    /**
     * The TextField where the user can input their username.
     */
    @FXML private TextField usernameField;

    /**
     * The ratio of the login box width to the parent width.
     */
    private double LOGIN_BOX_WIDTH_RATIO;

    /**
     * The ratio of the login box height to the parent height.
     */
    private double LOGIN_BOX_HEIGHT_RATIO;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setDefaultValue();

        usernameField.setOnAction(event -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                StatusEvent status = SetNickname.requester(Client.transceiver, new Object()).request(new SetNickname(MiniModel.getInstance().getUserID(), username));
                if (status.get().equals(MiniModel.getInstance().getErrorCode())) {
                    Stage currentStage = (Stage) parent.getScene().getWindow();
                    MessageController.showErrorMessage(currentStage, ((Pota) status).errorMessage());
                }
            }
        });

        // Bind the width and height of the loginBox to their original ratio
        loginBox.prefWidthProperty().bind(parent.widthProperty().multiply(LOGIN_BOX_WIDTH_RATIO));
        loginBox.prefHeightProperty().bind(parent.heightProperty().multiply(LOGIN_BOX_HEIGHT_RATIO));
        loginBox.maxWidthProperty().bind(parent.widthProperty().multiply(LOGIN_BOX_WIDTH_RATIO));
        loginBox.maxHeightProperty().bind(parent.heightProperty().multiply(LOGIN_BOX_HEIGHT_RATIO));
        loginBox.minWidthProperty().bind(parent.widthProperty().multiply(LOGIN_BOX_WIDTH_RATIO));
        loginBox.minHeightProperty().bind(parent.heightProperty().multiply(LOGIN_BOX_HEIGHT_RATIO));

        // Bind the titleImage to fit the width of the loginBox
        titleImage.fitWidthProperty().bind(loginBox.widthProperty());

        // Bind the usernameField width to the loginBox width
        usernameField.prefWidthProperty().bind(loginBox.widthProperty().multiply(0.5));
        usernameField.maxWidthProperty().bind(loginBox.widthProperty().multiply(0.5));
        usernameField.minWidthProperty().bind(loginBox.widthProperty().multiply(0.5));
    }

    private void setDefaultValue() {
        // Load the background image from resources
        URL imageUrl = getClass().getResource("/image/background/background1.png");
        if (imageUrl != null) {
            String cssBackground = "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                    "-fx-background-size: cover; " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;";
            parent.setStyle(cssBackground);
        }

        LOGIN_BOX_WIDTH_RATIO = loginBox.getPrefWidth() / parent.getPrefWidth();
        LOGIN_BOX_HEIGHT_RATIO = loginBox.getPrefHeight() / parent.getPrefHeight();
    }

    @Override
    public void react() {
    }
}
