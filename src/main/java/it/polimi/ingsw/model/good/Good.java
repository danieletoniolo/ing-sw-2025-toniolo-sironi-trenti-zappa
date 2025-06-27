package it.polimi.ingsw.model.good;

import java.io.Serializable;

/**
 * Represents a good item with a specific color/type.
 * This class implements Serializable to allow for object serialization.
 * @author Daniele Tonioli
 */
public class Good implements Serializable {
    /** The color/type of this good */
    private GoodType color;

    /**
     * Create a new good
     * @param color the color of the good
     */
    public Good(GoodType color) {
        this.color = color;
    }

    /**
     * Default constructor for Good.
     * Creates a new good with no initial color/type.
     */
    public Good(){

    }

    /**
     * Get the color of the good
     * @return the color of the good
     */
    public GoodType getColor() {
        return color;
    }

    /**
     * Get the value of the good
     * @return the value of the good
     */
    public int getValue() {
        return color.getValue();
    }

    /**
     * Compares this good with another object for equality.
     * Two goods are considered equal if they have the same color/type.
     *
     * @param good the object to compare with this good
     * @return true if the specified object is a Good with the same color, false otherwise
     */
    @Override
    public boolean equals(Object good) {
        if (good == null) {
            return false;
        }
        if (good instanceof Good) {
            return ((Good) good).getColor() == this.getColor();
        }
        return false;
    }
}