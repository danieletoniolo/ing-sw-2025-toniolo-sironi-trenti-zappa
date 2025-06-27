package it.polimi.ingsw.view.miniModel.cards.hit;

import it.polimi.ingsw.view.miniModel.good.GoodView;

/**
 * Enum representing different types of hits that can occur in the game.
 * Each hit type has an associated integer value for serialization purposes.
 */
public enum HitTypeView {
    /** Small meteor hit type with value 0 */
    SMALLMETEOR(0),
    /** Large meteor hit type with value 1 */
    LARGEMETEOR(1),
    /** Light fire hit type with value 2 */
    LIGHTFIRE(2),
    /** Heavy fire hit type with value 3 */
    HEAVYFIRE(3);

    /** The integer value associated with this hit type */
    private final int value;

    /**
     * Constructor for HitTypeView enum values.
     * @param value the integer value to associate with this hit type
     */
    HitTypeView(int value) {
        this.value = value;
    }

    /**
     * Gets the integer value associated with this hit type.
     * @return the integer value of this hit type
     */
    public int getValue() {
        return value;
    }

    /**
     * Creates a HitTypeView from its integer value.
     * @param value the integer value to convert
     * @return the corresponding HitTypeView enum value
     * @throws IllegalArgumentException if no HitTypeView exists with the given value
     */
    public static HitTypeView fromValue(int value) {
        for (HitTypeView hit : values()) {
            if (hit.value == value) {
                return hit;
            }
        }
        throw new IllegalArgumentException("No GoodView with value " + value);
    }

    /**
     * Returns a short string representation of this hit type for text-based user interface display.
     * @return a two-character string abbreviation of the hit type (e.g., "SM" for SMALLMETEOR)
     */
    public String drawTui() {
        return switch (this) {
            case SMALLMETEOR -> "SM";
            case LARGEMETEOR -> "LM";
            case LIGHTFIRE ->   "LF";
            case HEAVYFIRE ->   "HF";
        };
    }
}
