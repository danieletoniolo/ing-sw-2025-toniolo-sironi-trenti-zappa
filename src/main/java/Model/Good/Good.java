package Model.Good;

public class Good {
    private GoodType color;

    public Good(GoodType color) {
        this.color = color;
    }

    public GoodType getColor() {
        return color;
    }

    public int getValue() {
        return color.getValue();
    }
}