package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents the purple variant of the Life Support component in the TUI view.
 * Displays a colored asterisk in the center line.
 */
public class LifeSupportPurpleView extends ComponentView {
    /** ANSI escape code for purple color. */
    private static final String purple = "\033[35m";
    /** ANSI escape code to reset color. */
    private static final String reset = "\033[0m";

    /**
     * Constructs a LifeSupportPurpleView with the given ID, connectors, and orientation.
     *
     * @param ID         the component ID
     * @param connectors the connectors array
     * @param clockWise  the orientation
     */
    public LifeSupportPurpleView(int ID, int[] connectors, int clockWise) {
        super(ID, connectors, clockWise);
    }

    /**
     * Draws a specific line of the component for the TUI.
     * The middle line contains a purple asterisk.
     *
     * @param line the line number to draw
     * @return the string representation of the line
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + purple + " * " + reset + " " + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    /**
     * Gets the type of this component.
     *
     * @return the component type
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.LIFE_SUPPORT_PURPLE;
    }

    /**
     * Creates and returns a copy of this LifeSupportPurpleView.
     *
     * @return a clone of this instance
     */
    @Override
    public LifeSupportPurpleView clone() {
        LifeSupportPurpleView copy = new LifeSupportPurpleView(this.getID(), this.getConnectors(), this.getClockWise());
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
