package it.polimi.ingsw.view.miniModel.components;

import it.polimi.ingsw.view.gui.controllers.components.BatteryController;
import it.polimi.ingsw.view.gui.controllers.components.CabinController;
import it.polimi.ingsw.view.gui.controllers.components.ComponentController;
import it.polimi.ingsw.view.miniModel.components.crewmembers.CrewView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.javatuples.Pair;

import java.io.IOException;

/**
 * Represents a cabin component in the game view.
 * A cabin can contain crew members and has a specific color based on its ID.
 * Extends ComponentView to inherit basic component functionality.
 */
public class CabinView extends ComponentView {
    /** ANSI color code for light blue */
    private static final String lightBlue = "\033[94m";
    /** ANSI color code for blue */
    private static final String blue = "\033[34m";
    /** ANSI color code for green */
    private static final String green = "\033[32m";
    /** ANSI color code for yellow */
    private static final String yellow = "\033[33m";
    /** ANSI color code for red */
    private static final String red = "\033[31m";
    /** ANSI reset code to clear formatting */
    private static final String reset = "\033[0m";
    /** The color assigned to this cabin based on its ID */
    private final String color;

    /** Number of crew members currently in this cabin (0-2) */
    private int crewNumber;
    /** Type of crew member in this cabin */
    private CrewView crew;
    private Pair<Node, ComponentController> cabinPair;

    /**
     * Constructs a new CabinView with the specified parameters.
     * Assigns a color based on the cabin ID and initializes with no crew.
     *
     * @param ID the unique identifier for this cabin component
     * @param connectors array of connector positions for this component
     * @param clockWise the clockwise rotation value for this component
     */
    public CabinView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
        this.crewNumber = 0;
        switch (ID) {
            case 152 -> this.color = blue;
            case 153 -> this.color = green;
            case 154 -> this.color = red;
            case 155 -> this.color = yellow;
            default -> this.color = lightBlue;
        }
        crew = CrewView.HUMAN;
    }

    /**
     * Gets the number of crew members currently in this cabin.
     *
     * @return the number of crew members (0-2)
     */
    public int getCrewNumber() {
        return crewNumber;
    }

    /**
     * Sets the number of crew members currently in this cabin.
     *
     * @param crewNumber the number of crew members to set (should be 0-2)
     */
    public void setCrewNumber(int crewNumber) {
        this.crewNumber = crewNumber;
        notifyObservers();
    }

    /**
     * Sets the type of crew member in this cabin.
     *
     * @param crew the type of crew member to set
     */
    public void setCrewType(CrewView crew) {
        this.crew = crew;
        notifyObservers();
    }

    /**
     * Gets the type of crew member currently in this cabin.
     *
     * @return the crew member type
     */
    public CrewView getCrewType() {
        return crew;
    }

    /**
     * Checks if this cabin contains a purple alien crew member.
     *
     * @return true if the cabin has a purple alien, false otherwise
     */
    public boolean hasPurpleAlien() {
        return crew.equals(CrewView.PURPLEALIEN);
    }

    /**
     * Checks if this cabin contains a brown alien crew member.
     *
     * @return true if the cabin has a brown alien, false otherwise
     */
    public boolean hasBrownAlien() {
        return crew.equals(CrewView.BROWALIEN);
    }

    /**
     * Returns a pair containing the JavaFX Node and its associated controller for this cabin.
     * Loads the FXML layout if not already loaded and caches the result.
     *
     * @return a Pair containing the Node and ComponentController for this cabin
     */
    @Override
    public Pair<Node, ComponentController> getNode() {
        try {
            if (cabinPair != null) return cabinPair;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/components/cabin.fxml"));
            Parent root = loader.load();

            CabinController controller = loader.getController();
            controller.setModel(this);

            cabinPair = new Pair<>(root, controller);
            return cabinPair;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws a specific line of this cabin component for text-based UI display.
     * Returns the visual representation of the cabin including crew members
     * with appropriate coloring based on the cabin's assigned color.
     *
     * @param line the line number to draw (0, 1, or 2)
     * @return the formatted string representation of the specified line
     * @throws IndexOutOfBoundsException if line is not 0, 1, or 2
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + drawCrew() + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Draws the crew representation for this cabin component.
     * Creates a visual representation of the crew members inside the cabin
     * using ANSI color codes and text symbols.
     *
     * @return a formatted string showing the cabin with crew members
     *         - Empty cabin: colored parentheses with spaces
     *         - One crew: colored parentheses with one crew symbol
     *         - Two crew: colored parentheses with two crew symbols
     * @throws IllegalStateException if crewNumber is not 0, 1, or 2
     */
    private String drawCrew() {
        return switch (crewNumber) {
            case 0 -> color + "(" + reset + "   " + color + ")" + reset;
            case 1 -> color + "(" + reset + " " + crew.drawTui() + " " + color + ")" + reset;
            case 2 -> color + "(" + reset + crew.drawTui() + " " + crew.drawTui() + color + ")" + reset;
            default -> throw new IllegalStateException("Unexpected value: " + crewNumber);
        };
    }

    /**
     * Gets the type of this component.
     *
     * @return ComponentTypeView.CABIN indicating this is a cabin component
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.CABIN;
    }

    /**
     * Creates a deep copy of this CabinView instance.
     * The clone includes all properties: ID, connectors, rotation,
     * crew type, crew number, and wrong state.
     *
     * @return a new CabinView instance that is a copy of this cabin
     */
    @Override
    public CabinView clone() {
        CabinView copy = new CabinView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setCrewType(this.crew);
        copy.setCrewNumber(this.crewNumber);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
