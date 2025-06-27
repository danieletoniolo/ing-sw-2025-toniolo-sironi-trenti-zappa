package it.polimi.ingsw.view.miniModel;

/**
 * Represents a structure that can be drawn in a text-based user interface (TUI).
 */
public interface Structure {
    /**
     * Draws a specific line of the structure for the TUI.
     *
     * @param line the index of the line to draw
     * @return a string representing the specified line of the structure
     */
    String drawLineTui(int line);
}
