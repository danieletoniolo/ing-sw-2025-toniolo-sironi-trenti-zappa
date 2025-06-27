package it.polimi.ingsw.view.miniModel.components;

/**
 * Represents the view of an engine in the reduced model.
 * Extends {@link ComponentView} and manages the TUI display of the engine,
 * distinguishing between single and double engines.
 */
public class EngineView extends ComponentView {
    /** ANSI color code for brown. */
    private static final String brown = "\033[38;5;220m";
    /** ANSI code to reset color. */
    private static final String reset = "\033[0m";
    /** Indicates if the engine is double. */
    private final boolean doubleEngine;

    /**
     * Constructor for EngineView.
     * @param ID component identifier
     * @param connectors array of connectors
     * @param clockWise orientation
     * @param power engine power (2 if double, otherwise single)
     */
    public EngineView(int ID, int[] connectors, int clockWise, int power) {
        super(ID, connectors, clockWise);
        this.doubleEngine = power == 2;
    }

    /**
     * Checks if the engine is double.
     * @return true if double, false otherwise
     */
    public boolean isDoubleEngine() {
        return doubleEngine;
    }

    /**
     * Draws a line of the engine component for the TUI.
     * @param line index of the line to draw
     * @return string representing the line
     */
    @Override
    public String drawLineTui(int line) {
        if (isCovered()) return super.drawLineTui(line);

        return switch (line) {
            case 0, 2 -> super.drawLineTui(line);
            case 1 -> super.drawLeft(line) + (doubleEngine ? drawDoubleEngine() : drawSingleEngine()) + super.drawRight(line);
            default -> throw new IndexOutOfBoundsException("Unexpected value: " + line);
        };
    }

    /**
     * Draws the single engine representation for the TUI.
     * The arrow direction depends on the engine's orientation.
     * @return a string with the colored arrow for a single engine
     */
    private String drawSingleEngine(){
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> "  " + brown + ArrowUp + reset + "  ";
            case 1 -> "  " + brown + ArrowRight + reset + "  ";
            case 2 -> "  " + brown + ArrowDown + reset + "  ";
            case 3 -> "  " + brown + ArrowLeft + reset + "  ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    /**
     * Draws the double engine representation for the TUI.
     * The arrows' direction depends on the engine's orientation.
     * @return a string with two colored arrows for a double engine
     */
    private String drawDoubleEngine(){
        return switch ((getClockWise() + 2) % getConnectors().length) {
            case 0 -> " " + brown + ArrowUp + reset + " " + brown + ArrowUp + reset + " ";
            case 1 -> " " + brown + ArrowRight + reset + " " + brown + ArrowRight + reset + " ";
            case 2 -> " " + brown + ArrowDown + reset + " " + brown + ArrowDown + reset + " ";
            case 3 -> " " + brown + ArrowLeft + reset + " " + brown + ArrowLeft + reset + " ";
            default -> throw new IllegalStateException("Unexpected value: " + (getClockWise() + 2) % getConnectors().length);
        };
    }

    /**
     * Returns the type of the engine component.
     * @return ComponentTypeView.DOUBLE_ENGINE if double, otherwise ComponentTypeView.SINGLE_ENGINE
     */
    @Override
    public ComponentTypeView getType() {
        return doubleEngine ? ComponentTypeView.DOUBLE_ENGINE : ComponentTypeView.SINGLE_ENGINE;
    }

    /**
     * Creates and returns a copy of this EngineView.
     * @return a clone of this instance
     */
    @Override
    public EngineView clone() {
        EngineView copy = new EngineView(this.getID(), this.getConnectors(), this.getClockWise(), this.doubleEngine ? 2 : 1);
        copy.setIsWrong(this.getIsWrong());
        return copy;
    }
}
