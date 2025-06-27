package it.polimi.ingsw.view.miniModel.spaceship;

import it.polimi.ingsw.view.miniModel.Structure;
import it.polimi.ingsw.view.miniModel.components.ComponentView;

import java.util.ArrayList;

/**
 * Represents a view for the discard or reserved pile in the spaceship mini model.
 * Handles the display and management of reserved components and their discard state.
 */
public class DiscardReservedPileView implements Structure {
    /**
     * List of reserved components in the pile.
     */
    private final ArrayList<ComponentView> reserved;

    /**
     * Indicates if the pile is a discard pile.
     */
    private boolean isDiscard;

    /**
     * Constructs an empty DiscardReservedPileView, initially set as a reserved pile.
     */
    public DiscardReservedPileView() {
        reserved = new ArrayList<>();
        isDiscard = false;
    }

    /**
     * Sets the pile as a discard pile.
     */
    public void setIsDiscarded(){
        isDiscard = true;
    }

    /**
     * Returns the number of rows to draw for the pile in the TUI.
     * @return the number of rows to draw
     */
    public int getRowsToDraw() {
        return ComponentView.getRowsToDraw() + 2;
    }

    /**
     * Draws a specific line of the pile for the TUI.
     * @param line the line number to draw
     * @return the string representation of the line
     */
    @Override
    public String drawLineTui(int line) {
        StringBuilder str = new StringBuilder();
        String Up =   "╭──────────────╮";
        String Side = "│";
        String down = "╰──────────────╯";
        String Clear = "       ";

        switch (line) {
            case 0:
                String name = isDiscard ? "Discard pile: " : "Reserved pile: ";
                str.append(name).append(getReserved().size());
                break;
            case 1:
                str.append(Up);
                break;
            case 2, 3, 4:
                line -= 2;
                str.append(Side);
                str.append(reserved.isEmpty() ? Clear : reserved.getFirst().drawLineTui(line));
                str.append(reserved.isEmpty() || reserved.size() == 1 ? Clear : reserved.getLast().drawLineTui(line));
                str.append(Side);
                break;
            case 5:
                str.append(down);
                break;
        }

        return str.toString();
    }

    /**
     * Adds a component to the reserved pile.
     * @param component the component to add
     */
    public void addDiscardReserved(ComponentView component) {
        reserved.add(component);
    }

    /**
     * Removes a component from the reserved pile by its ID.
     * @param ID the ID of the component to remove
     * @return the removed component, or null if not found
     */
    public ComponentView removeDiscardReserved(int ID) {
        int i;
        boolean found = false;
        for (i = 0; i < reserved.size(); i++) {
            if (reserved.get(i).getID() == ID) {
                found = true;
                break;
            }
        }

        return found ? reserved.remove(i) : null;
    }

    /**
     * Returns the list of reserved components.
     * @return the list of reserved components
     */
    public ArrayList<ComponentView> getReserved() {
        return reserved;
    }
}
