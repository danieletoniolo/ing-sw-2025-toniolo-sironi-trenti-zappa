package Model.Good;

import java.io.Serializable;

public class Good implements Serializable {
    private GoodType color;

    /**
     * Create a new good
     * @param color the color of the good
     */
    public Good(GoodType color) {
        this.color = color;
    }

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