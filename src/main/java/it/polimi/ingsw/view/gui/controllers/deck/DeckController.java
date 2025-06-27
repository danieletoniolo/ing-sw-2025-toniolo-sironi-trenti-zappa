package it.polimi.ingsw.view.gui.controllers.deck;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import it.polimi.ingsw.view.miniModel.deck.DeckView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DeckController implements MiniModelObserver, Initializable {
    /**
     * The StackPane that serves as the parent container for the deck.
     */
    @FXML private StackPane parent;

    /**
     * The Pane that contains the cards in the deck.
     */
    @FXML private Pane deckPane;

    /**
     * A list of StackPanes representing the individual cards in the deck.
     * This is used to manage the layout of the cards when they are displayed.
     */
    private List<Node> cardPanes;

    /**
     * The DeckView model associated with this controller.
     * It is set via the setModel method after the FXML has been loaded.
     */
    private DeckView deckView;

    /**
     * Initializes the controller after the FXML has been loaded.
     * Sets up the layout and bindings for the deck and card panes.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parent.setMinSize(0, 0);
        parent.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        deckPane.setMinSize(0, 0);
        deckPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Bind the deckPane's width and height to the parent's dimensions
        deckPane.prefWidthProperty().bind(parent.widthProperty());
        deckPane.prefHeightProperty().bind(parent.heightProperty());

        // Bind the cardPanes' width and height to the deckPane's dimensions
        for (Node cardPane : deckPane.getChildren()) {
            if (cardPane instanceof StackPane stackPane) {
                double paddingRatio = 20.0 / 351.0;
                stackPane.prefWidthProperty().bind(deckPane.widthProperty().multiply(1 - paddingRatio));
                stackPane.prefHeightProperty().bind(deckPane.heightProperty());
            }
        }

        // Save the children of the deckPane to cardPanes
        cardPanes = new ArrayList<>(deckPane.getChildren());
        cardPanes.sort((a, b) -> {
            if (a != null && b != null) {
                return Integer.compare(Integer.parseInt(b.getId()), Integer.parseInt(a.getId()));
            }
            return 0; // Default case if not both are StackPane
        });
    }

    /**
     * Sets the {@link DeckView} model for this controller and registers it as an observer.
     * This method should be called after the FXML has been loaded.
     *
     * @param deckView the DeckView model to set
     * @apiNote This method should be called after the FXML has been loaded
     *          to ensure that the card panes are initialized and ready to be updated.
     */
    public void setModel(DeckView deckView) {
        this.deckView = deckView;
        this.deckView.registerObserver(this);
        this.react();
    }

    /**
     * Reacts to changes in the DeckView model.
     * This method updates the card panes based on the current state of the deck.
     */
    @Override
    public void react() {
        Platform.runLater(() -> {
            if (deckView.getDeck().isEmpty()) return;

            if (deckView.isCovered() || deckView.isOnlyLast()) {
                int i = 0;
                for (CardView cv : deckView.getDeck()) {
                    StackPane cardPane = (StackPane) cardPanes.get(i);
                    cardPane.getChildren().clear();
                    cardPane.getChildren().add(cv.getNode().getValue0());
                    i++;
                }
            }
        });
    }

    /**
     * Returns the parent StackPane of this controller.
     *
     * @return the parent StackPane
     */
    public Node getParent() {
        return parent;
    }
}
