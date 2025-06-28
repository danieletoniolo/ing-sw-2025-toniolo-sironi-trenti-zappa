package it.polimi.ingsw.view.gui.controllers.misc;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    /**
     * The StackPane that serves as the parent container for the message.
     */
    @FXML private StackPane messageRoot;

    /**
     * The HBox that contains the message text and close button.
     */
    @FXML private HBox messageBox;

    /**
     * The Label that displays the message text.
     */
    @FXML private Label messageText;

    /**
     * The Button that allows the user to close the message.
     */
    @FXML private Button closeButton;

    /**
     * Initializes the controller class.
     * This method is called by the FXMLLoader when the FXML file is loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set the handlers for the close button
        closeButton.setOnAction(event -> closeMessage());

        // Set the message box to disappear after a delay of 5 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> closeMessage());
        delay.play();

        // Make the message box responsive to the width of the root container
        messageBox.prefWidthProperty().bind(messageRoot.widthProperty().multiply(0.95));
    }


    /**
     * Sets the message text to be displayed in the message box.
     * @param message The message text to be displayed.
     */
    public void setMessage(String message) {
        messageText.setText(message);
    }

    /**
     * Closes the message box with a fade-out animation and hides the window.
     */
    private void closeMessage() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), messageRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> messageRoot.getScene().getWindow().hide());
        fadeOut.play();
    }

    /**
     * Displays a message popup with the specified content and styling.
     * The popup is automatically positioned relative to the owner window and includes
     * responsive behavior for window resizing.
     *
     * @param owner The parent window that will own the popup
     * @param message The text message to display in the popup
     * @param path The path to the FXML file that defines the popup's appearance
     */
    private static void showMessage(Window owner, String message, String path) {
        try {
            // Load the FXML file for the message popup
            FXMLLoader loader = new FXMLLoader(MessageController.class.getResource(path));
            StackPane messagePane = loader.load();
            MessageController controller = loader.getController();
            controller.setMessage(message);

            // Create a popup to display the message
            Popup popup = new Popup();
            popup.getContent().add(messagePane);
            popup.setAutoHide(true);

            // Initialize the popup position and size
            positionPopup(owner, popup, messagePane);

            // Add a listener to the owner window to reposition the popup when the window is resized
            owner.widthProperty().addListener((obs, oldVal, newVal) -> {
                double newWidth = Math.min(900, newVal.doubleValue() * 0.9);
                messagePane.setPrefWidth(newWidth);
                positionPopup(owner, popup, messagePane);
            });

            // Ensure the popup is positioned correctly when the owner window is moved
            positionPopup(owner, popup, messagePane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Positions the popup window relative to its owner window.
     * If the popup is already showing, updates its position. Otherwise, shows the popup
     * at the calculated position. The popup is centered horizontally and positioned
     * 50 pixels from the top of the owner window.
     *
     * @param owner The parent window used as reference for positioning
     * @param popup The popup window to be positioned
     * @param messagePane The content pane of the popup, used for width calculations
     */
    private static void positionPopup(Window owner, Popup popup, StackPane messagePane) {
        if (popup.isShowing()) {
            // Update position of already visible popup
            popup.setX(owner.getX() + (owner.getWidth() - messagePane.getWidth()) / 2);
            popup.setY(owner.getY() + 50);
        } else {
            // Show popup at calculated position
            popup.show(owner,
                    owner.getX() + (owner.getWidth() - messagePane.getWidth()) / 2,
                    owner.getY() + 50);
        }
    }

    /**
     * Sets the message text to be displayed in the window chosen by the user.
     * @param owner The owner window where the message will be displayed.
     * @param message The message text to be displayed in the popup window.
     */
    public static void showInfoMessage(Window owner, String message) {
        showMessage(owner, message, "/fxml/misc/messageInfo.fxml");
    }

    /**
     * Sets the message text to be displayed in the window chosen by the user.
     * @param owner The owner window where the message will be displayed.
     * @param message The message text to be displayed in the popup window.
     */
    public static void showErrorMessage(Window owner, String message) {
        showMessage(owner, message, "/fxml/misc/messageError.fxml");
    }
}
