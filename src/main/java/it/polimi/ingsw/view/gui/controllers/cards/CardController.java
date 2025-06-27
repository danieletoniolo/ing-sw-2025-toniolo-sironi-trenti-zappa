package it.polimi.ingsw.view.gui.controllers.cards;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller class for managing card display and interactions in the GUI.
 *
 * This controller handles the visual representation of cards, including:
 * - Loading and displaying card images (both covered and uncovered states)
 * - Implementing drag and drop functionality for card movement
 * - Responding to changes in the underlying CardView model
 * - Managing responsive UI scaling and layout
 *
 * The controller implements MiniModelObserver to receive updates when the
 * associated CardView changes, and Initializable to set up the UI components
 * after FXML loading.
 */
public class CardController implements MiniModelObserver, Initializable {
    /**
     * The StackPane that serves as the parent container for the card image.
     */
    @FXML private StackPane parent;

    /**
     * The ImageView that displays the card image.
     */
    @FXML
    private ImageView cardImage;

    /**
     * The Rectangle used to clip the card image.
     */
    @FXML private Rectangle clipRect;

    /**
     * The CardView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    private CardView cardView;
    
    /**
     * Initializes the controller after its root element has been completely processed.
     * This method sets up the UI components and configures drag and drop functionality for the card image.
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources The resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure parent container sizing to allow flexible resizing
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind card image dimensions to parent container for responsive scaling
        cardImage.fitWidthProperty().bind(parent.widthProperty());
        cardImage.fitHeightProperty().bind(parent.heightProperty());

       // Drag & drop setup for the card image
       cardImage.setOnDragDetected(event -> {
           // Start drag operation with MOVE transfer mode
           Dragboard db = cardImage.startDragAndDrop(TransferMode.MOVE);
           ClipboardContent content = new ClipboardContent();

           // Store the card ID as drag data for identification during drop
           content.putString(String.valueOf(cardView.getID()));
           db.setContent(content);

           // Set visual feedback during drag using a snapshot of the card image
           db.setDragView(cardImage.snapshot(null, null),
                          event.getX(), event.getY());

           // Consume the event to prevent further processing
           event.consume();
       });
    }

   /**
    * Sets the {@link CardView} model for this controller and registers it as an observer.
    *
    * @param   cardView the CardView model to set
    * @apiNote This method should be called after the FXML has been loaded
    *          to ensure that the cardImage ImageView is initialized.
    */
    public void setModel(CardView cardView) {
       this.cardView = cardView;
       this.cardView.registerObserver(this);
       this.react();
    }

    /**
     * Reacts to changes in the CardView model.
     * This method updates the card image based on the current state of the card.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            String path;
            // Update the image based on the card model
            if (cardView.isCovered()) {
                path = switch (cardView.getLevel()) {
                    case 1 -> "/image/card/covered_1.jpg";
                    case 2 -> "/image/card/covered_2.jpg";
                    default -> throw new IllegalStateException("Unexpected value: " + cardView.getLevel());
                };
            } else {
                path = "/image/card/" + cardView.getID() + ".jpg";
            }
            Image img = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            cardImage.setImage(img);
        });
    }
}