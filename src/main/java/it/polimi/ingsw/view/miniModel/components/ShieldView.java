package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents a shield component in the mini model view.
 * Handles the display and state of shields in the TUI.
 */
public class ShieldView extends ComponentView {
    /** Unicode character for the upper shield. */
    public static final String UpShield = "∩";
    /** Unicode character for the lower shield. */
    public static final String DownShield = "∪";
    /** Unicode character for the left shield. */
    public static final String LeftShield = "(";
    /** Unicode character for the right shield. */
    public static final String RightShield = ")";
    /** ANSI escape code for light green color. */
    private static final String lightGreen = "\033[92m";
    /** ANSI escape code to reset color. */
    private static final String reset = "\033[0m";
    /** Array representing the presence of shields in four directions. */
    private boolean[] shields;

    /**
     * Constructs a ShieldView.
     * @param ID the component ID
     * @param connectors the connectors array
     * @param clockWise the clockwise rotation
     * @param shields boolean array indicating shield presence (up, left, down, right)
     */
    public ShieldView(int ID, int[] connectors, int clockWise, boolean[] shields) {
        super(ID, connectors, clockWise);
        this.shields = shields;
    }

    /**
     * Gets the shield states.
     * @return boolean array representing shield presence
     */
    public boolean[] getShields() {
        return shields;
    }

    /**
     * Sets the shield states.
     * @param shields boolean array representing shield presence
     */
    public void setShields(boolean[] shields) {
        this.shields = shields;
    }

    /**
     * Draws a specific line of the component for the TUI.
     * @param line the line index to draw
     * @return the string representation of the line
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + drawShield() + super.drawRight(line);
            default -> throw new IllegalStateException("Unexpected value: " + line);
        };
    }

    /**
     * Draws the shield symbols for the TUI.
     * @return the string representation of the shields
     */
    private String drawShield() {
        StringBuilder str = new StringBuilder();
        str.append(" ");
        if (shields[1]) {
            str.append(lightGreen).append(LeftShield).append(reset);
            if (shields[0]) str.append(lightGreen).append(" ").append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(" ").append(DownShield).append(reset);
        }
        if (shields[3]) {
            if (shields[0]) str.append(lightGreen).append(UpShield).append(reset);
            if (shields[2]) str.append(lightGreen).append(DownShield).append(reset);
            str.append(" ").append(lightGreen).append(RightShield).append(reset);
        }
        str.append(" ");
        return str.toString();
    }

    /**
     * Gets the type of the component.
     * @return the component type view
     */
    @Override
    public ComponentTypeView getType() {
        return ComponentTypeView.SHIELD;
    }

    /**
     * Creates a clone of this ShieldView.
     * @return a new ShieldView with the same properties
     */
    @Override
    public ShieldView clone() {
        ShieldView copy = new ShieldView(this.getID(), this.getConnectors(), this.getClockWise(), this.shields);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
