package it.polimi.ingsw.view.miniModel.cards.hit;

import it.polimi.ingsw.view.miniModel.good.GoodView;

/**
 * Enumeration representing the four cardinal directions for hit actions.
 * Each direction is associated with a numeric value and has corresponding arrow symbols for TUI display.
 */
public enum HitDirectionView {
    /** North direction with value 0 */
    NORTH(0),
    /** West direction with value 1 */
    WEST(1),
    /** South direction with value 2 */
    SOUTH(2),
    /** East direction with value 3 */
    EAST(3);

    /** Arrow symbol pointing right */
    public static String ArrowRight = "→";
    /** Arrow symbol pointing down */
    public static String ArrowDown  = "↓";
    /** Arrow symbol pointing left */
    public static String ArrowLeft  = "←";
    /** Arrow symbol pointing up */
    public static String ArrowUp    = "↑";
    /** The numeric value associated with this direction */
    private final int value;

    /**
     * Constructor for HitDirectionView enum constant.
     *
     * @param value the numeric value associated with this direction
     */
    HitDirectionView(int value) {
        this.value = value;
    }

    /**
     * Gets the numeric value associated with this direction.
     *
     * @return the numeric value of this direction
     */
    public int getValue() {
        return value;
    }

    /**
     * Creates a HitDirectionView from its numeric value.
     *
     * @param value the numeric value to convert (0-3)
     * @return the corresponding HitDirectionView enum constant
     * @throws IllegalArgumentException if the value doesn't correspond to any direction
     */
    public static HitDirectionView fromValue(int value) {
        for (HitDirectionView hit : values()) {
            if (hit.value == value) {
                return hit;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    /**
     * Returns the arrow symbol representation for TUI display.
     * Note: The arrows are inverted compared to the direction names for game logic purposes.
     *
     * @return the Unicode arrow symbol corresponding to this direction
     */
    public String drawTui() {
        return switch (this) {
            case NORTH -> ArrowDown;
            case SOUTH -> ArrowUp;
            case EAST -> ArrowLeft;
            case WEST -> ArrowRight;
        };
    }
}
