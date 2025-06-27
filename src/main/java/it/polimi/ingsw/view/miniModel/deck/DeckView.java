package it.polimi.ingsw.view.miniModel.deck;

import it.polimi.ingsw.view.gui.controllers.deck.DeckController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.cards.CardView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Represents a view of a deck of cards in the mini model.
 * Manages the stack of cards, observer registration, and GUI node creation.
 */
public class DeckView implements MiniModelObservable {
    /** Stack containing the cards in the deck. */
    private final Stack<CardView> deck;
    /** Indicates if the deck is covered (face down). */
    private boolean covered;
    /** Indicates if only the last card should be shown. */
    private boolean onlyLast;
    /** List of observers registered to this deck. */
    private final List<MiniModelObserver> observers;
    /** Pair containing the JavaFX node and its controller for the deck. */
    private Pair<Node, DeckController> deckNode;

    /**
     * Constructs an empty DeckView and initializes the observer list.
     */
    public DeckView() {
        this.deck = new Stack<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to this deck.
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from this deck.
     * @param observer the observer to remove
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change in the deck.
     */
    @Override
    public void notifyObservers() {
        synchronized (observers) {
            for (MiniModelObserver observer : observers) {
                observer.react();
            }
        }
    }

    /**
     * Returns the JavaFX node and controller for the deck, loading them if necessary.
     * @return a Pair containing the Node and DeckController, or null if loading fails
     */
    public Pair<Node, DeckController> getNode() {
        try {
            if (deckNode != null) return deckNode;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards/deck.fxml"));
            Node root = loader.load();

            DeckController controller = loader.getController();
            controller.setModel(this);

            deckNode = new Pair<>(root, controller);
            return deckNode;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Checks if the deck is currently covered (face down).
     * @return true if the deck is covered, false otherwise
     */
    public boolean isCovered() {
        return covered;
    }

    /**
     * Sets whether the deck is covered (face down) and updates all cards accordingly.
     * Notifies observers after the change.
     * @param covered true to cover the deck, false to uncover
     */
    public void setCovered(boolean covered) {
        this.covered = covered;

        for (CardView card : deck) {
            card.setCovered(covered);
        }
        notifyObservers();
    }

    /**
     * Checks if only the last card in the deck should be shown.
     * @return true if only the last card is shown, false otherwise
     */
    public boolean isOnlyLast() {
        return onlyLast;
    }

    /**
     * Sets whether only the last card in the deck should be shown.
     * If set to true, uncovers the last card.
     * Notifies observers after the change.
     * @param onlyLast true to show only the last card, false otherwise
     */
    public void setOnlyLast(boolean onlyLast) {
        this.onlyLast = onlyLast;

        if (onlyLast) {
            deck.peek().setCovered(false);
        }
        notifyObservers();
    }

    /**
     * Adds a card to the top of the deck and notifies observers.
     * @param card the CardView to add
     */
    public void addCard(CardView card) {
        deck.push(card);
        notifyObservers();
    }

    /**
     * Removes the card from the top of the deck.
     */
    public void popCard() {
        deck.pop();
    }

    /**
     * Returns the stack of cards in the deck.
     * @return the stack of CardView objects
     */
    public Stack<CardView> getDeck() {
        return deck;
    }

    /**
     * Reorders the deck according to the given list of card IDs.
     * Notifies observers after reordering.
     * @param ids the list of card IDs in the desired order
     */
    public void order(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            int j;
            for (j = 0; j < deck.size(); j++) {
                if (deck.get(j).getID() == ids.get(i)) {
                    break;
                }
            }
            Collections.swap(deck, j, i);
        }
        notifyObservers();
    }

    /**
     * Gets the number of rows to draw for the deck (static).
     * @return the number of rows to draw
     */
    public static int getRowsToDraw() {
        return CardView.getRowsToDraw();
    }

    /**
     * Gets the number of columns to draw for the deck.
     * @return the number of columns to draw
     */
    public int getColsToDraw() {
        return CardView.getColsToDraw() + deck.size() - 1;
    }

    /**
     * Draws a specific line of the deck for the text-based user interface (TUI).
     * @param line the line number to draw
     * @return the string representation of the specified line
     */
    public String drawLineTui(int line){
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < deck.size(); i++) {
            if (isCovered()) {
                if (i == deck.size() - 1) {
                    str.append(deck.get(i).drawLineTui(line));
                } else {
                    str.append(deck.get(i).drawLineTui(line).charAt(0));
                }
            } else if (onlyLast) {
                if (i == deck.size() - 1) {
                    str.append(deck.get(i).drawLineTui(line));
                } else {
                    str.append(deck.get(i).drawLineTui(line).charAt(0));
                }
            } else {
                str.append(deck.get(i).drawLineTui(line));
            }
        }

        return str.toString();
    }
}
