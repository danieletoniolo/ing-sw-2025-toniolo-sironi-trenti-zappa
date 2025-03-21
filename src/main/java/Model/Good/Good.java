package Model.Good;

public class Good {
    private GoodType color;

    /**
     * Create a new good
     * @param color the color of the good
     */
    public Good(GoodType color) {
        this.color = color;
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
}