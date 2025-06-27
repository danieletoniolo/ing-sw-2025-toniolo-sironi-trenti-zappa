package it.polimi.ingsw.view.miniModel.player;

import it.polimi.ingsw.model.player.PlayerColor;
import it.polimi.ingsw.view.gui.controllers.misc.PlayerMarkerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Enum representing the possible player marker colors for the view.
 * Each marker has an associated integer value and ANSI color codes for TUI rendering.
 */
public enum MarkerView {
    RED(3), YELLOW(2), GREEN(1), BLUE(0);

    // ANSI color codes for TUI representation
    private final String blue =   "\033[34m";
    private final String green =  "\033[32m";
    private final String yellow = "\033[33m";
    private final String red =    "\033[31m";
    private final String reset =  "\033[0m";
    private final String player = "◉";
    private int value;

    /**
     * Constructs a MarkerView with the specified value.
     * @param value the integer value associated with the marker color
     */
    MarkerView(int value) {
        this.value = value;
    }

    /**
     * Loads and returns the JavaFX Node representing this marker.
     * @return the Node for the marker, or null if loading fails
     */
    public Node getNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/misc/playerMarker.fxml"));
            Parent root = loader.load();

            PlayerMarkerController controller = loader.getController();
            controller.setModel(this);

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the integer value associated with this marker color.
     *
     * @return the value of the marker color
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the MarkerView corresponding to the specified integer value.
     *
     * @param value the integer value of the marker color
     * @return the MarkerView with the given value
     * @throws IllegalArgumentException if the value does not correspond to any MarkerView
     */
    public static MarkerView fromValue(int value) {
        for (MarkerView color : MarkerView.values()) {
            if (color.value == value) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    /**
     * Returns a string representation of the marker for TUI (Text User Interface),
     * using ANSI color codes and a player symbol.
     *
     * @return the colored player marker as a string for TUI display
     */
    public String drawTui() {
        return switch (this) {
            case RED -> red + player + reset;
            case YELLOW -> yellow + player + reset;
            case GREEN -> green + player + reset;
            case BLUE -> blue + player + reset;
        };
    }
}
