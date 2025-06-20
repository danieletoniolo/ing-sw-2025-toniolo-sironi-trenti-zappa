package it.polimi.ingsw.view.gui.controllers.deck;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class DeckController implements MiniModelObserver {

    @FXML private Pane deckPane;

    private DeckView deckView;

    public void setModel(DeckView deckView) {
        this.deckView = deckView;
        this.deckView.registerObserver(this);
        this.react();
    }

    @Override
    public void react() {
        deckPane.getChildren().clear();

        if (deckView.getDeck().isEmpty()) return;

        if (deckView.isCovered() || deckView.isOnlyLast()) {
            // If the deck is covered or only the last card is visible, we stack the card one on top of the other
            double xOffset = 0;
            for (CardView cv : deckView.getDeck()) {
                Node node = cv.getNode();
                node.setLayoutX(xOffset);
                deckPane.getChildren().add(node);
                xOffset += 10;
            }
        }
    }
}
