package it.polimi.ingsw.view.gui.controllers.cards;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.Objects;

public class CardController implements MiniModelObserver {
    /**
     * The ImageView that displays the card image.
     */
    @FXML
    private ImageView cardImage;

    /**
     * The CardView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    private CardView cardView;
    
    @FXML
    private void initialize() {
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
    * Sets the model for this controller and loads the corresponding card image.
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
        cardImage.setFitWidth(cardView.getWidth());
    }
}