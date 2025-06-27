package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents the brown variant of the Life Support component in the view layer.
 * Handles the TUI (Text User Interface) drawing logic and type identification.
 */
public class LifeSupportBrownView extends ComponentView {

    /** ANSI escape code for brown color. */
    private static final String brown = "\033[38;5;220m";
    /** ANSI escape code to reset color. */
    private static final String reset = "\033[0m";

    /**
     * Constructs a LifeSupportBrownView with the specified ID, connectors, and orientation.
     *
     * @param ID         the unique identifier of the component
     * @param connectors the array of connector values
     * @param clockWise  the orientation of the component
     */
    public LifeSupportBrownView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    /**
     * Draws a specific line of the component for the TUI.
     *
     * @param line the line number to draw
     * @return the string representation of the line for the TUI
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + brown + " * " + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    /**
     * Returns the type of this component.
     *
     * @return the component type as ComponentTypeView.LIFE_SUPPORT_BROWN
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.LIFE_SUPPORT_BROWN;
    }

    /**
     * Creates and returns a copy of this LifeSupportBrownView.
     *
     * @return a clone of this instance
     */
    @Override
    public LifeSupportBrownView clone() {
        LifeSupportBrownView copy = new LifeSupportBrownView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
