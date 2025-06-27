package it.polimi.ingsw.view.miniModel.components.crewmembers;

import it.polimi.ingsw.view.miniModel.MiniModelObserver;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum representing different types of crew members with their visual representations
 * and observer pattern implementation for GUI updates.
 * Each crew type has a unique value and can be rendered in both GUI and TUI modes.
 */
public enum CrewView {
    /** Human crew member (value: 0) */
    HUMAN(0),
    /** Brown alien crew member (value: 1) */
    BROWALIEN(1),
    /** Purple alien crew member (value: 2) */
    PURPLEALIEN(2),
    /** Uncolored alien crew member (value: 3) */
    UNCOLOREDALIEN(3);

    /** List of observers that react to changes in this crew view */
    private final List<MiniModelObserver> listeners = new ArrayList<>();
    /** Unique identifier value for this crew type */
    private final int value;
    /** ANSI color code for brown text in terminal */
    private final String brown = "\033[38;5;220m";
    /** ANSI color code for purple text in terminal */
    private final String purple = "\033[35m";
    /** ANSI reset code to return to default terminal color */
    private final String reset = "\033[0m";

    /**
     * Constructor for CrewView enum values.
     * @param value the unique identifier for this crew type
     */
    CrewView(int value) {
        this.value = value;
    }

    /**
     * Gets the unique identifier value for this crew type.
     * @return the integer value associated with this crew type
     */
    public int getValue() {
        return value;
    }


    /**
     * Adds an observer to the list of listeners that will be notified of changes.
     * @param listener the observer to add to the notification list
     */
    public void addListener(MiniModelObserver listener) {
        listeners.add(listener);
    }

    /**
     * Removes an observer from the list of listeners.
     * @param listener the observer to remove from the notification list
     */
    public void removeListener(MiniModelObserver listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all registered observers by calling their react() method.
     * This method is called internally when the crew view state changes.
     */
    private void notifyListeners() {
        for (MiniModelObserver listener : listeners) {
            listener.react();
        }
    }

    /**
     * Creates a CrewView instance from its corresponding integer value.
     * @param value the integer value representing the crew type
     * @return the CrewView enum constant that matches the given value
     * @throws IllegalArgumentException if no CrewView exists with the specified value
     */
    public static CrewView fromValue(int value) {
        for (CrewView crew : values()) {
            if (crew.value == value) {
                return crew;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    /**
     * Renders the crew member(s) on a GUI canvas and returns the updated image.
     * Draws colored circles representing crew members at the specified position.
     * For human crew members with count of 2, draws two circles side by side.
     * @param image the base image to draw on
     * @param x the x-coordinate for the center position of the crew representation
     * @param y the y-coordinate for the center position of the crew representation
     * @param size the size parameter used to calculate the radius of the crew circles
     * @param numberOfCrewMembers the number of crew members to draw (affects layout for humans)
     * @return a new Image with the crew member(s) drawn on it
     */
    public Image drawGui(Image image, int x, int y, int size, int numberOfCrewMembers) {
        Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);

        Color fillColor = switch (this) {
            case HUMAN -> Color.WHITE;
            case BROWALIEN -> Color.rgb(95, 75, 25);
            case PURPLEALIEN -> Color.rgb(133, 25, 133);
            case UNCOLOREDALIEN -> Color.rgb(150, 150, 150);
        };

        Color borderColor = Color.BLACK;
        double radius = size / 8;

        gc.setLineWidth(2);
        gc.setStroke(borderColor);
        gc.setFill(fillColor);

        if (this == CrewView.HUMAN && numberOfCrewMembers == 2) {
            double spacing = 5;
            double offset = radius + (spacing / 2);

            gc.fillOval(x - offset - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x - offset - radius, y - radius, radius * 2, radius * 2);

            gc.fillOval(x + offset - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x + offset - radius, y - radius, radius * 2, radius * 2);
        } else {
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        // Scrivi il disegno finale in una nuova WritableImage
        WritableImage result = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        canvas.snapshot(null, result);
        return result;
    }

    /**
     * Renders the crew member as a text character for Terminal User Interface (TUI) display.
     * Returns colored ANSI text for aliens and a simple Unicode character for humans.
     * @return a string representation of the crew member with appropriate coloring for terminal display
     */
    public String drawTui() {
        return switch (this) {
            case HUMAN -> "☺";
            case BROWALIEN -> brown + "&" + reset;
            case PURPLEALIEN -> purple + "&" + reset;
            case UNCOLOREDALIEN -> "&";
        };
    }
}
