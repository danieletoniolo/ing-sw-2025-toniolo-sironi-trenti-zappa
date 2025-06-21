package it.polimi.ingsw.view.gui.controllers.cards;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.util.Objects;

public class CardController implements MiniModelObserver {
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
    
    @FXML
    private void initialize() {
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        cardImage.fitWidthProperty().bind(parent.widthProperty());
        cardImage.fitHeightProperty().bind(parent.heightProperty());

       // Drag & drop setup for the card image
       cardImage.setOnDragDetected(event -> {
           Dragboard db = cardImage.startDragAndDrop(TransferMode.MOVE);
           ClipboardContent content = new ClipboardContent();

           content.putString(String.valueOf(cardView.getID()));
           db.setContent(content);

           db.setDragView(cardImage.snapshot(null, null),
                          event.getX(), event.getY());

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
    }
}