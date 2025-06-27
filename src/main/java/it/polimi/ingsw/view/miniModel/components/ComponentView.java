package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.miniModel.MiniModelObservable;
import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import it.polimi.ingsw.view.miniModel.Structure;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a component view in the mini model.
 * Handles TUI representation, rotation, covered state, and observer notification.
 * Implements Structure and MiniModelObservable.
 */
public abstract class ComponentView implements Structure, MiniModelObservable {
    /** Graphical representation of the top part without connectors */
    public static String Up0 =   "╭─────╮";
    /** Graphical representation of the top part with one connector */
    public static String Up1 =   "╭──|──╮";
    /** Graphical representation of the top part with two connectors */
    public static String Up2 =   "╭─|─|─╮";
    /** Graphical representation of the top part with three connectors */
    public static String Up3 =   "╭─|||─╮";

    /** Graphical representation of the bottom part without connectors */
    public static String Down0 = "╰─────╯";
    /** Graphical representation of the bottom part with one connector */
    public static String Down1 = "╰──|──╯";
    /** Graphical representation of the bottom part with two connectors */
    public static String Down2 = "╰─|─|─╯";
    /** Graphical representation of the bottom part with three connectors */
    public static String Down3 = "╰─|||─╯";

    /** Arrow pointing right */
    public static String ArrowRight = "→";
    /** Arrow pointing down */
    public static String ArrowDown = "↓";
    /** Arrow pointing left */
    public static String ArrowLeft = "←";
    /** Arrow pointing up */
    public static String ArrowUp = "↑";

    /** Side without connector */
    public static String[] Side0 = {
            ".",
            "│",
            "."
    };

    /** Side with single connector */
    public static String[] Side1 = {
            ".",
            "─",
            "."
    };

    /** Side with double connector */
    public static String[] Side2 = {
            ".",
            "═",
            "."
    };

    /** Side with triple connector */
    public static String[] Side3 = {
            ".",
            "≣",
            "."
    };

    /** Array of the component's connectors (clockwise order) */
    private int[] connectors;
    /** Component identifier */
    private int ID;
    /** Indicates if the component is covered */
    private boolean covered;
    /** Row where the component is located */
    private int row;
    /** Column where the component is located */
    private int col;
    /** Indicates if the component is in an error state */
    private boolean isWrong;
    /** Red color code for TUI */
    private final String red = "\033[31m";
    /** Reset color code for TUI */
    private final String reset = "\033[0m";
    /** Number of clockwise rotations */
    private int clockWise;
    /** List of registered observers */
    private final List<MiniModelObserver> observers;
    /** Pair containing the graphical node and the associated controller */
    private Pair<Node, ComponentController> componentPair;

    /**
     * Constructor for the ComponentView class.
     * @param ID component identifier
     * @param connectors array of connectors
     * @param clockWise number of clockwise rotations
     */
    public ComponentView(int ID, int[] connectors, int clockWise) {
        this.ID = ID;
        this.connectors = connectors;
        this.covered = false;
        this.clockWise = clockWise;
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to this component.
     * @param observer the observer to register
     */
    @Override
    public void registerObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Unregisters an observer from this component.
     * @param observer the observer to unregister
     */
    @Override
    public void unregisterObserver(MiniModelObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers of a change in this component.
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
     * Returns a pair containing the JavaFX Node representing the component and its associated controller.
     * Loads the FXML if not already loaded and caches the result.
     * @return Pair of Node and ComponentController, or null if loading fails
     */
    public Pair<Node, ComponentController> getNode() {
        try {
            if (componentPair != null) return componentPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/component.fxml"));
            Parent root = loader.load();

            ComponentController controller = loader.getController();
            controller.setModel(this);

            componentPair = new Pair<>(root, controller);
            return componentPair;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Rotates an image 90 degrees clockwise.
     * @param inputImage the input image to rotate
     * @return a new image rotated 90 degrees clockwise
     */
    public Image rotateImage(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(height, width);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();

        // Ruota in senso orario
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setArgb(height - y - 1, x, reader.getArgb(x, y));
            }
        }
        return outputImage;
    }

    /**
     * Draws a specific line of the component for the TUI (Text User Interface).
     * The line parameter determines which part of the component to draw (top, middle, or bottom).
     * @param line the line index to draw (0 = top, 1 = middle, 2 = bottom)
     * @return the string representation of the specified line
     * @throws IndexOutOfBoundsException if the line index is not valid
     */
    @Override
    public String drawLineTui(int line) throws IndexOutOfBoundsException{
        String str = switch (line) {
            case 0 -> isCovered() || connectors[0] == 0 ? Up0 : connectors[0] == 1 ? Up1 : connectors[0] == 2 ? Up2 : Up3;
            case 1 -> drawLeft(line) + "  ?  " + drawRight(line);
            case 2 -> isCovered() || connectors[2] == 0 ? Down0 : connectors[2] == 1 ? Down1 : connectors[2] == 2 ? Down2 : Down3;
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };

        return isWrong ? red + str + reset : str;
    }

    /**
     * Returns the number of rows to draw for the TUI representation of the component.
     * @return the number of rows (always 3)
     */
    public static int getRowsToDraw() {
        return 3;
    }

    /**
     * Draws the left side of the component for the specified line in the TUI.
     * @param line the line index (0 = top, 1 = middle, 2 = bottom)
     * @return the string representation of the left side for the given line
     */
    protected String drawLeft(int line) {
        if (isCovered()) return Side0[line];
        String str = switch (connectors[1]) {
            case 0 -> Side0[line];
            case 1 -> Side1[line];
            case 2 -> Side2[line];
            case 3 -> Side3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + connectors[0]);
        };

        return isWrong ? red + str + reset : str;
    }

    /**
     * Draws the right side of the component for the specified line in the TUI.
     * @param line the line index (0 = top, 1 = middle, 2 = bottom)
     * @return the string representation of the right side for the given line
     */
    protected String drawRight(int line) {
        if (isCovered()) return Side0[line];
        String str = switch (connectors[3]) {
            case 0 -> Side0[line];
            case 1 -> Side1[line];
            case 2 -> Side2[line];
            case 3 -> Side3[line];
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + connectors[0]);
        };

        return isWrong ? red + str + reset : str;
    }

    /**
     * Sets the error state of the component.
     * @param isWrong true if the component is in an error state, false otherwise
     */
    public void setIsWrong(boolean isWrong) {
        this.isWrong = isWrong;
    }

    /**
     * Returns whether the component is in an error state.
     * @return true if the component is in an error state, false otherwise
     */
    public boolean getIsWrong() {
        return isWrong;
    }

    /**
     * Sets the identifier of the component.
     * @param ID the new identifier
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Returns the identifier of the component.
     * @return the component ID
     */
    public int getID() {
        return ID;
    }

    /**
     * Sets the connectors array for the component.
     * @param connectors the new connectors array
     */
    public void setConnectors(int[] connectors) {
        this.connectors = connectors;
    }

    /**
     * Returns the connectors array of the component.
     * @return the connectors array
     */
    public int[] getConnectors() {
        return connectors;
    }

    /**
     * Sets whether the component is covered.
     * @param covered true if the component is covered, false otherwise
     */
    public void setCovered(boolean covered) {
        this.covered = covered;
    }

    /**
     * Returns whether the component is covered.
     * @return true if the component is covered, false otherwise
     */
    public boolean isCovered() {
        return covered;
    }

    /**
     * Returns the type of the component.
     * @return the component type
     */
    public abstract ComponentTypeView getType();

    /**
     * Sets the row position of the component.
     * @param row the row index
     */
    public void setRow(int row) {
        this.row = row + 1;
    }

    /**
     * Returns the row position of the component.
     * @return the row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the column position of the component.
     * @param col the column index
     */
    public void setCol(int col) {
        this.col = col + 1;
    }

    /**
     * Returns the column position of the component.
     * @return the column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Creates and returns a copy of this component.
     * @return a clone of this component
     */
    public abstract ComponentView clone();

    /**
     * Rotates the component clockwise and notifies observers.
     */
    public void rotate() {
        this.clockWise++;
        this.clockWise = this.clockWise % this.connectors.length;
        notifyObservers();
    }

    /**
     * Returns the number of clockwise rotations applied to the component.
     * @return the number of clockwise rotations
     */
    public int getClockWise() {
        return clockWise;
    }
}
