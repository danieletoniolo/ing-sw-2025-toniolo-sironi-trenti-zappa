package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents a cannon component in the game view.
 * A cannon can be either single or double based on its power and orientation.
 */
public class CannonView extends ComponentView {
    /** The power level of the cannon */
    private final float power;
    /** ANSI color code for purple text */
    private final String purple = "\033[35m";
    /** ANSI reset code to clear formatting */
    private final String reset = "\033[0m";
    /** Whether this cannon is a double cannon */
    private final boolean doubleCannon;

    /**
     * Constructs a new CannonView with the specified parameters.
     *
     * @param ID the unique identifier of the cannon
     * @param connectors array of connector positions
     * @param clockWise the orientation of the cannon (0=up, 1=right, 2=down, 3=left)
     * @param power the power level of the cannon
     */
    public CannonView(int ID, int[] connectors, int clockWise, float power) {
        super(ID, connectors, clockWise);
        this.power = power;
        this.doubleCannon = (getClockWise() == 0 && power == 2) || (getClockWise() != 0 && power == 1);
    }

    /**
     * Draws a specific line of the cannon component for text-based UI.
     *
     * @param line the line number to draw (0-2)
     * @return the string representation of the specified line
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + " " + (doubleCannon ? drawDoubleCannon() : drawCannon()) + " " + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Draws a single cannon symbol based on its orientation.
     * The cannon is displayed with purple color and surrounded by spaces.
     *
     * @return the string representation of a single cannon with proper spacing
     * @throws IllegalStateException if the clockwise value is not between 0-3
     */
    private String drawCannon(){
        return switch (getClockWise()) {
            case 0 -> " " + purple + ArrowUp + reset + " ";
            case 1 -> " " + purple + ArrowRight + reset + " ";
            case 2 -> " " + purple + ArrowDown + reset + " ";
            case 3 -> " " + purple + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + getClockWise());
        };
    }

    /**
     * Draws a double cannon symbol based on its orientation.
     * The double cannon is displayed as two adjacent cannon symbols with purple color.
     *
     * @return the string representation of a double cannon
     * @throws IllegalStateException if the clockwise value is not between 0-3
     */
    private String drawDoubleCannon(){
        return switch (getClockWise()) {
            case 0 -> purple + ArrowUp + reset + " " + purple + ArrowUp + reset;
            case 1 -> purple + ArrowRight + reset + " " + purple + ArrowRight + reset;
            case 2 -> purple + ArrowDown + reset + " " + purple + ArrowDown + reset;
            case 3 -> purple + ArrowLeft + reset + " " + purple + ArrowLeft + reset;
            default -> throw new IllegalStateException("Unexpected value: " + getClockWise());
        };
    }

    /**
     * Gets the component type based on whether this is a single or double cannon.
     *
     * @return ComponentTypeView.DOUBLE_CANNON if this is a double cannon,
     *         ComponentTypeView.SINGLE_CANNON otherwise
     */
    @Override
    public ComponentTypeView getType() {
        return doubleCannon ? ComponentTypeView.DOUBLE_CANNON : ComponentTypeView.SINGLE_CANNON;
    }

    /**
     * Creates a deep copy of this CannonView instance.
     * The cloned cannon will have the same ID, connectors, orientation, power,
     * and wrong state as the original.
     *
     * @return a new CannonView instance that is a copy of this one
     */
    @Override
    public CannonView clone() {
        CannonView copy = new CannonView(this.getID(), this.getConnectors(), this.getClockWise(), this.power);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
