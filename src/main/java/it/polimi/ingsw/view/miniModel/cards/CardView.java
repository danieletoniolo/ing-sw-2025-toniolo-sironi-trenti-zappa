package it.polimi.ingsw.view.miniModel.cards;

import it.polimi.ingsw.view.gui.controllers.cards.CardController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a view of a card in the game.
 * This class implements the Structure interface for TUI drawing capabilities
 * and MiniModelObservable for observer pattern notifications.
 *
 * CardView provides common functionality for all card types including:
 * - TUI drawing with predefined card layouts
 * - Observer pattern implementation for model updates
 * - JavaFX node creation and management
 * - Card state management (covered/uncovered, level, dimensions)
 */
public abstract class CardView implements Structure, MiniModelObservable {
    /**
     * This string is used to draw the top of the card in the TUI.
     */
    protected static String Up =       "╭───────────────────╮";

    /**
     * This string is used to draw an empty line of the card in the TUI.
     */
    protected static String Clear =    "│                   │";

    /**
     * These strings are used to draw a covered card in the TUI for level 1.
     */
    private static final String Covered1 = "│      ───┬───      │";
    private static final String Covered2 = "│         │         │";
    private static final String Covered3 = "│         │         │";
    private static final String Covered4 = "│      ───┴───      │";

    /**
     * These strings are used to draw a covered card in the TUI for level 2.
     */
    private static final String Covered5 = "│      ─┬───┬─      │";
    private static final String Covered6 = "│       │   │       │";
    private static final String Covered7 = "│       │   │       │";
    private static final String Covered8 = "│      ─┴───┴─      │";

    /**
     * This string is used to draw the bottom of the card in the TUI.
     */
    public static String Down =     "╰───────────────────╯";

    private final int ID;
    private boolean covered;
    private final int level;

    private double width;

    /**
     * This list contains the observers that are registered to this CardView.
     * It is used to notify the observers when the model changes.
     */
    private final List<MiniModelObserver> listeners;
    private Pair<Node, CardController> cardPair;

    /**
     * Constructs a new CardView with the specified properties.
     * Initializes the card with its unique identifier, coverage state, and level.
     * Also initializes the observers list for the observer pattern implementation.
     *
     * @param ID the unique identifier for this card
     * @param covered true if the card should be initially covered, false otherwise
     * @param level the level of the card (1 or 2)
     */
    public CardView(int ID, boolean covered, int level) {
        this.ID = ID;
        this.covered = covered;
        this.level = level;
        this.listeners = new ArrayList<>();
    }

    /**
     * Registers a new observer to this CardView.
     * The observer will be notified when the card's state changes.
     * This method is thread-safe and uses synchronization on the listeners list.
     *
     * @param observer the observer to register for notifications
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (listeners) {
            listeners.add(observer);
            listeners.notifyAll();
        }
    }

    /**
     * Unregisters an observer from this CardView.
     * The observer will no longer receive notifications when the card's state changes.
     * This method is thread-safe and uses synchronization on the listeners list.
     *
     * @param observer the observer to unregister from notifications
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (listeners) {
            listeners.remove(observer);
            listeners.notifyAll();
        }
    }

    /**
     * Notifies all registered observers that the card's state has changed.
     * This method iterates through all registered observers and calls their react() method.
     * The notification process is thread-safe and uses synchronization on the listeners list.
     */
    @Override
    public void notifyObservers() {
        synchronized (listeners) {
            for (MiniModelObserver observer : listeners) {
                observer.react();
            }
        }
    }

    /**
     * Gets the level of this card.
     * The level determines the visual representation of the card when covered.
     *
     * @return the level of the card (1 or 2)
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Gets the current width of this card view.
     * This property is used for layout and positioning in the UI.
     *
     * @return the width of the card as a double value
     */
    public double getWidth() {
        return width;
    }

    /**
     * Sets the width of this card view and notifies observers of the change.
     * This method triggers observer notifications to update any dependent UI components.
     *
     * @param width the new width value to set for the card
     */
    public void setWidth(double width) {
        this.width = width;
        notifyObservers();
    }

    /**
     * Creates and returns a JavaFX Node paired with its controller for this card.
     * Uses lazy initialization - if the node has already been created, returns the cached pair.
     * Loads the card FXML template and sets up the controller with this CardView as the model.
     *
     * @return a Pair containing the JavaFX Node and its CardController, or null if loading fails
     */
    public Pair<Node, CardController> getNode() {
        try {
            if (cardPair != null) return cardPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards/card.fxml"));
            Parent root = loader.load();

            CardController controller = loader.getController();
            controller.setModel(this);

            cardPair = new Pair<>(root, controller);
            return cardPair;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the number of rows required to draw this card in the TUI.
     * This method returns a constant value representing the height of a card
     * in text-based user interface rendering.
     *
     * @return the number of rows (10) needed to draw a complete card
     */
    public static int getRowsToDraw() {
        return 10;
    }

    /**
     * Gets the number of columns required to draw this card in the TUI.
     * This method returns a constant value representing the width of a card
     * in text-based user interface rendering.
     *
     * @return the number of columns (21) needed to draw a complete card
     */
    public static int getColsToDraw() {
        return 21;
    }

    /**
     * Draws a specific line of the card in the TUI.
     * This method implements the Structure interface and provides the visual
     * representation of each line of the card. Returns different string patterns
     * based on the line number and card level for covered cards.
     *
     * @param line the line number to draw (0-9, where 0 is top and 9 is bottom)
     * @return the string representation of the specified line, or null for invalid lines
     */
    @Override
    public String drawLineTui(int line){
        return switch(line) {
            case 0 -> Up;
            case 3 -> (level == 1) ? Covered1 : Covered5;
            case 4 -> (level == 1) ? Covered2 : Covered6;
            case 5 -> (level == 1) ? Covered3 : Covered7;
            case 6 -> (level == 1) ? Covered4 : Covered8;
            case 1, 2, 7, 8 -> Clear;
            case 9 -> Down;
            default -> null;
        };
    }

    /**
     * Gets the unique identifier of this card.
     * The ID is immutable and set during card construction.
     *
     * @return the unique identifier of the card
     */
    public int getID() {
        return ID;
    }

    /**
     * Checks whether this card is currently covered.
     * Covered cards display a generic pattern instead of their actual content.
     *
     * @return true if the card is covered, false if it's revealed
     */
    public boolean isCovered() {
        return covered;
    }

    /**
     * Sets the covered state of this card and notifies observers of the change.
     * When a card's coverage state changes, all registered observers are notified
     * to update their representations accordingly.
     *
     * @param covered true to cover the card, false to reveal it
     */
    public void setCovered(boolean covered) {
        this.covered = covered;
        notifyObservers();
    }

    /**
     * Gets the type of this card view.
     * This abstract method must be implemented by concrete card view subclasses
     * to specify their specific card type.
     *
     * @return the specific type of this card view
     */
    public abstract CardViewType getCardViewType();
}
