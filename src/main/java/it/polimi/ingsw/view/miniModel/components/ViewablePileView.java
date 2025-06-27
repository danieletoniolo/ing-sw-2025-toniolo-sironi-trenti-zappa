package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.misc.ViewablePileController;
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
 * Represents a viewable pile in the mini model, observable by MiniModelObservers.
 * Manages a collection of ComponentView objects and notifies observers on changes.
 * Provides a JavaFX node and controller for GUI representation.
 */
public class ViewablePileView implements Structure, MiniModelObservable {
    /** List of components that can be viewed in the pile. */
    private final List<ComponentView> viewableComponents;
    /** List of observers registered to this pile. */
    private final List<MiniModelObserver> observers;
    /** Number of columns for displaying components. */
    private final int cols = 21;
    /** Cached pair of JavaFX node and its controller for the pile view. */
    private Pair<Node, ViewablePileController> viewablePileNode;

    /**
     * Constructs an empty ViewablePileView.
     */
    public ViewablePileView() {
        this.viewableComponents = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to be notified of changes.
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer so it will no longer receive notifications.
     * @param observer the observer to unregister
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change.
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
     * Returns the JavaFX node and its controller for this pile view.
     * Loads the FXML if not already loaded.
     * @return a Pair containing the Node and its ViewablePileController
     */
    public Pair<Node, ViewablePileController> getNode() {
        try {
            if (viewablePileNode != null) return viewablePileNode;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/viewablePile.fxml"));
            Node node = loader.load();

            ViewablePileController controller = loader.getController();
            controller.setModel(this);

            viewablePileNode = new Pair<>(node, controller);
            return viewablePileNode;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a component to the pile and notifies observers of the change.
     * @param component the ComponentView to add
     */
    public void addComponent(ComponentView component) {
        viewableComponents.add(component);
        notifyObservers();
    }

    /**
     * Removes a component from the pile and notifies observers of the change.
     * @param component the ComponentView to remove
     */
    public void removeComponent(ComponentView component) {
        viewableComponents.remove(component);
        notifyObservers();
    }

    /**
     * Returns a copy of the list of viewable components in the pile.
     * @return an ArrayList containing the viewable components
     */
    public ArrayList<ComponentView> getViewableComponents() {
        return new ArrayList<>(viewableComponents);
    }

    /**
     * Returns the number of columns used to display the components.
     * @return the number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Calculates the number of rows needed to draw all components in the pile.
     * @return the number of rows to draw
     */
    public int getRowsToDraw() {
        int rows = 1 + (viewableComponents.size() / cols) * ComponentView.getRowsToDraw();
        if (viewableComponents.size() % cols != 0 || viewableComponents.isEmpty()) {
            rows += ComponentView.getRowsToDraw();
        }
        return rows;
    }

    /**
     * Draws a specific line of the pile for the text-based user interface (TUI).
     * @param l the line number to draw
     * @return a String representing the drawn line
     */
    @Override
    public String drawLineTui(int l) {
        StringBuilder line = new StringBuilder();

        if (l == 0) {
            line.append("   ");
            for (int i = 0; i < cols; i++) {
                line.append("  ").append((i + 1) / 10 == 0 ? " " + (i + 1) : (i + 1)).append("   ");
            }
            return line.toString();
        }


        l -= 1; // Adjust for header line
        int h = l / ComponentView.getRowsToDraw();
        int i = l % ComponentView.getRowsToDraw();

        if (h < viewableComponents.size() / cols) {
            if (i == 1) {
                line.append(((h + 1) / 10 == 0 ? ((h + 1) + "  ") : (h + 1) + " "));
            } else {
                line.append("   ");
            }
            for (int k = 0; k < cols; k++) {
                line.append(viewableComponents.get(h * cols + k).drawLineTui(i));
            }
            return line.toString();
        }

        if (viewableComponents.size() % cols != 0 || viewableComponents.isEmpty()) {
            if (i == 1) {
                line.append(((viewableComponents.size() / cols + 1) / 10 == 0 ? ((viewableComponents.size() / cols + 1) + "  ") : ((viewableComponents.size() / cols + 1) + " ")));
            } else {
                line.append("   ");
            }
            for (int k = 0; k < viewableComponents.size() % cols; k++) {
                line.append(viewableComponents.get((viewableComponents.size() / cols) * cols + k).drawLineTui(i));
            }
            return line.toString();
        }

        return line.toString();
    }
}
